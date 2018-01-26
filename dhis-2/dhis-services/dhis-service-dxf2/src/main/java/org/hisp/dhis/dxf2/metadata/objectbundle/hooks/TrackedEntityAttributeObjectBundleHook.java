package org.hisp.dhis.dxf2.metadata.objectbundle.hooks;

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata.objectbundle.ObjectBundle;
import org.hisp.dhis.feedback.ErrorCode;
import org.hisp.dhis.feedback.ErrorReport;
import org.hisp.dhis.render.DeviceRenderTypeMap;
import org.hisp.dhis.render.RenderDevice;
import org.hisp.dhis.render.type.ValueTypeRenderingObject;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

import java.util.ArrayList;
import java.util.List;

public class TrackedEntityAttributeObjectBundleHook
    extends AbstractObjectBundleHook
{

    /* TODO: Merge later
    @Override
    public <T extends IdentifiableObject> List<ErrorReport> validate( T object, ObjectBundle bundle )
    {

        if ( object instanceof TrackedEntityAttribute )
        {
            TrackedEntityAttribute attribute = (TrackedEntityAttribute) object;

            if ( attribute.isGenerated() )
            {
                if ( attribute.getPattern() == null || attribute.getPattern().isEmpty() )
                {
                    return Lists.newArrayList(
                        new ErrorReport( TrackedEntityAttribute.class, ErrorCode.E4018, "pattern", "generated",
                            true ) );
                }
                try
                {
                    TextPatternParser.parse( attribute.getPattern() );
                }
                catch ( TextPatternParser.TextPatternParsingException e )
                {
                    return Lists.newArrayList(
                        new ErrorReport( TrackedEntityAttribute.class, ErrorCode.E4019, attribute.getPattern(),
                            e.getMessage() )
                    );
                }
            }
        }

        return new ArrayList<>();
    }
    */

    @Override
    public <T extends IdentifiableObject> List<ErrorReport> validate( T object, ObjectBundle bundle )
    {
        List<ErrorReport> errorReports = new ArrayList<>();

        /*
         * Validate that the RenderType (if any) conforms to the constraints of ValueType or OptionSet.
         */
        if ( object != null && object.getClass().isAssignableFrom( TrackedEntityAttribute.class ) )
        {
            TrackedEntityAttribute attr = (TrackedEntityAttribute) object;
            DeviceRenderTypeMap<ValueTypeRenderingObject> map = attr.getRenderType();

            if ( map == null )
            {
                return errorReports;
            }

            for ( RenderDevice device : map.keySet() )
            {
                if ( map.get( device ).getType() == null )
                {
                    errorReports
                        .add( new ErrorReport( TrackedEntityAttribute.class, ErrorCode.E4011, "renderType.type" ) );
                }

                if ( !ValidationUtils
                    .validateRenderingType( TrackedEntityAttribute.class, attr.getValueType(), attr.hasOptionSet(),
                        map.get( device ).getType() ) )
                {
                    errorReports.add( new ErrorReport( TrackedEntityAttribute.class, ErrorCode.E4020,
                        map.get( device ).getType(), attr.getValueType() ) );
                }

            }
        }

        return errorReports;

    }
}
