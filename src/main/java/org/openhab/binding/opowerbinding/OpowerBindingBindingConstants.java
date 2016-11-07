/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.opowerbinding;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link OpowerBindingBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mikhail Popov - Initial contribution
 */
public class OpowerBindingBindingConstants {

    public static final String BINDING_ID = "opowerbinding";

    public static final String WEEATHER_USAGE_FORECAST_THING_TYPE_ID = "wuf";

    public static final String PEAK_HOURS_NOTIFICATION_THING_TYPE_ID = "phnotification";

    public final static ThingTypeUID THING_TYPE_WEATHER_USAGE_FORECAST = new ThingTypeUID(BINDING_ID,
            WEEATHER_USAGE_FORECAST_THING_TYPE_ID);

    public final static ThingTypeUID THING_TYPE_NOTIFICATION = new ThingTypeUID(BINDING_ID,
            PEAK_HOURS_NOTIFICATION_THING_TYPE_ID);

    public final static String CHANNEL_BILL_PERIOD = "channelBillPeriod";

    public final static String CHANNEL_ESTIMATED_COST = "channelEstimatedCost";

    public final static String CHANNEL_PROJECTED_COST = "channelProjectedCost";

    public final static String CHANNEL_ESTIMATED_USAGE = "channelEstimatedUsage";

    public final static String CHANNEL_PROJECTED_USAGE = "channelProjectedUsage";

    public final static String CHANNEL_PEAK_HOURS_NOTIFICATION = "channelPeakHoursNotification";

}
