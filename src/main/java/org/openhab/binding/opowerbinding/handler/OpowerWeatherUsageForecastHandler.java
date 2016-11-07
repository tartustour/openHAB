/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.opowerbinding.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openhab.binding.opowerbinding.OpowerBindingBindingConstants;
import org.openhab.binding.opowerbinding.model.Insight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

/**
 * The {@link OpowerWeatherUsageForecastHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mikhail Popov - Initial contribution
 */
public class OpowerWeatherUsageForecastHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(OpowerWeatherUsageForecastHandler.class);

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets
            .newHashSet(OpowerBindingBindingConstants.THING_TYPE_WEATHER_USAGE_FORECAST);

    private static final String PARAM_URL = "url";

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");
    private static final Integer DEFAULT_REFRESH_VALUE = 60;

    private String url;
    private BigDecimal refresh;

    private Insight insight = null;

    ScheduledFuture<?> refreshJob;

    public OpowerWeatherUsageForecastHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        Configuration config = getThing().getConfiguration();

        this.url = (String) config.get(PARAM_URL);

        try {
            refresh = (BigDecimal) config.get("refresh");
        } catch (Exception e) {
            logger.debug("Cannot set refresh parameter.", e);
        }

        if (refresh == null) {
            refresh = new BigDecimal(DEFAULT_REFRESH_VALUE);
        }

        startAutomaticRefresh();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            boolean success = updateInsightData();
            if (success) {
                switch (channelUID.getId()) {
                    case OpowerBindingBindingConstants.CHANNEL_BILL_PERIOD:
                        updateState(channelUID, getBillPeriod());
                        break;
                    case OpowerBindingBindingConstants.CHANNEL_ESTIMATED_COST:
                        updateState(channelUID, getEstimatedCost());
                        break;
                    case OpowerBindingBindingConstants.CHANNEL_PROJECTED_COST:
                        updateState(channelUID, getProjectedCost());
                        break;
                    case OpowerBindingBindingConstants.CHANNEL_ESTIMATED_USAGE:
                        updateState(channelUID, getEstimatedUsage());
                        break;
                    case OpowerBindingBindingConstants.CHANNEL_PROJECTED_USAGE:
                        updateState(channelUID, getProjectedUsage());
                        break;
                    default:
                        logger.debug("Command received for an unknown channel: {}", channelUID.getId());
                        break;
                }
            }
        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }
    }

    private void startAutomaticRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = updateInsightData();
                    if (success) {
                        updateState(
                                new ChannelUID(getThing().getUID(), OpowerBindingBindingConstants.CHANNEL_BILL_PERIOD),
                                getBillPeriod());
                        updateState(new ChannelUID(getThing().getUID(),
                                OpowerBindingBindingConstants.CHANNEL_ESTIMATED_COST), getEstimatedCost());
                        updateState(new ChannelUID(getThing().getUID(),
                                OpowerBindingBindingConstants.CHANNEL_PROJECTED_COST), getProjectedCost());
                        updateState(new ChannelUID(getThing().getUID(),
                                OpowerBindingBindingConstants.CHANNEL_ESTIMATED_USAGE), getEstimatedUsage());
                        updateState(new ChannelUID(getThing().getUID(),
                                OpowerBindingBindingConstants.CHANNEL_PROJECTED_USAGE), getProjectedUsage());
                    }
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh.intValue(), TimeUnit.SECONDS);
    }

    private synchronized boolean updateInsightData() {
        try {
            String insightData = getInsightData();
            if (insightData != null) {
                ObjectMapper mapper = new ObjectMapper();
                this.insight = mapper.readValue(insightData, Insight.class);
                updateStatus(ThingStatus.ONLINE);
                return true;
            }
        } catch (IOException e) {
            logger.warn("Error accessing Opower data: {}", e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
        }
        return false;
    }

    private String getInsightData() throws IOException {
        try {
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            return IOUtils.toString(connection.getInputStream());
        } catch (MalformedURLException e) {
            logger.debug("The url '{}' is not valid: {}", this.url, e.getMessage());
            return null;
        }
    }

    private State getBillPeriod() {
        if (this.insight != null) {
            String periodStart = DATE_TIME_FORMATTER.print(this.insight.getBillPeriodBegin());
            String periodEnd = DATE_TIME_FORMATTER.print(this.insight.getBillPeriodEnd());
            if (periodStart != null && periodEnd != null) {
                return new StringType(String.format("%s - %s", periodStart, periodEnd));
            }
        }
        return UnDefType.UNDEF;
    }

    private State getEstimatedCost() {
        if (this.insight != null) {
            Integer estimatedCost = this.insight.getEstimatedCost();
            String currency = this.insight.getCurrency();
            if (estimatedCost != null && currency != null) {
                return new StringType(String.format("%s %s", estimatedCost, currency));
            }
        }
        return UnDefType.UNDEF;
    }

    private State getProjectedCost() {
        if (this.insight != null) {
            Integer projectedCost = this.insight.getProjectedCost();
            String currency = this.insight.getCurrency();
            if (projectedCost != null && currency != null) {
                return new StringType(String.format("%s %s", projectedCost, currency));
            }
        }
        return UnDefType.UNDEF;
    }

    private State getEstimatedUsage() {
        if (this.insight != null) {
            Integer estimatedUsage = this.insight.getEstimatedUsage();
            if (estimatedUsage != null) {
                return new StringType(String.format("%s kWh", estimatedUsage));
            }
        }
        return UnDefType.UNDEF;
    }

    private State getProjectedUsage() {
        if (this.insight != null) {
            Integer projectedUsage = this.insight.getProjectedUsage();
            if (projectedUsage != null) {
                return new StringType(String.format("%s kWh", projectedUsage));
            }
        }
        return UnDefType.UNDEF;
    }
}
