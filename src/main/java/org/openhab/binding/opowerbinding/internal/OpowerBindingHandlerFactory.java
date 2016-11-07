/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.opowerbinding.internal;

import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.opowerbinding.OpowerBindingBindingConstants;
import org.openhab.binding.opowerbinding.handler.OpowerPeakHoursNotificationHandler;
import org.openhab.binding.opowerbinding.handler.OpowerWeatherUsageForecastHandler;

import com.google.common.collect.Sets;

/**
 * The {@link OpowerBindingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mikhail Popov - Initial contribution
 */
public class OpowerBindingHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Sets.union(
            OpowerWeatherUsageForecastHandler.SUPPORTED_THING_TYPES,
            OpowerPeakHoursNotificationHandler.SUPPORTED_THING_TYPES);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(OpowerBindingBindingConstants.THING_TYPE_WEATHER_USAGE_FORECAST)) {
            return new OpowerWeatherUsageForecastHandler(thing);
        }

        if (thingTypeUID.equals(OpowerBindingBindingConstants.THING_TYPE_NOTIFICATION)) {
            return new OpowerPeakHoursNotificationHandler(thing);
        }

        return null;
    }
}
