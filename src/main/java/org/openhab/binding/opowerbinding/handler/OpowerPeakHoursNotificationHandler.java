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
import org.eclipse.smarthome.core.library.types.OnOffType;
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
import org.openhab.binding.opowerbinding.OpowerBindingBindingConstants;
import org.openhab.binding.opowerbinding.model.Insight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

/**
 * The {@link OpowerPeakHoursNotificationHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mikhail Popov - Initial contribution
 */
public class OpowerPeakHoursNotificationHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(OpowerPeakHoursNotificationHandler.class);

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets
            .newHashSet(OpowerBindingBindingConstants.THING_TYPE_NOTIFICATION);

    private static final String PARAM_URL = "url";

    private static final Integer DEFAULT_REFRESH_VALUE = 60;

    private String url;
    private BigDecimal refresh;

    private Insight insight = null;

    ScheduledFuture<?> refreshJob;

    public OpowerPeakHoursNotificationHandler(Thing thing) {
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
                    case OpowerBindingBindingConstants.CHANNEL_PEAK_HOURS_NOTIFICATION:
                        updateState(channelUID, getNotificationState());
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
                                new ChannelUID(getThing().getUID(),
                                        OpowerBindingBindingConstants.CHANNEL_PEAK_HOURS_NOTIFICATION),
                                getNotificationState());
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

    private State getNotificationState() {
        if (this.insight != null) {
            Boolean isActivePeakEnergyUsage = this.insight.getIsActivePeakEnergyUsage();
            return Boolean.TRUE.equals(isActivePeakEnergyUsage) ? OnOffType.ON : OnOffType.OFF;
        }
        return UnDefType.UNDEF;
    }

}
