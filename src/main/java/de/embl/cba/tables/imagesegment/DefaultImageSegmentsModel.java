/*-
 * #%L
 * Various Java code for ImageJ
 * %%
 * Copyright (C) 2018 - 2021 EMBL
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package de.embl.cba.tables.imagesegment;

import de.embl.cba.tables.imagesegment.ImageSegment;
import de.embl.cba.tables.imagesegment.ImageSegmentsModel;
import de.embl.cba.tables.imagesegment.LabelFrameAndImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultImageSegmentsModel< T extends ImageSegment > implements ImageSegmentsModel< T >
{
	private Map< LabelFrameAndImage, T > idToSegment;
	private String modelName;

	public DefaultImageSegmentsModel( List< T > imageSegments, String modelName )
	{
		this.modelName = modelName;
		createSegmentMap( imageSegments );
	}

	public void createSegmentMap( List< T > imageSegments )
	{
		idToSegment = new HashMap<>();
		for ( T imageSegment : imageSegments )
		{
			final LabelFrameAndImage key = new LabelFrameAndImage( imageSegment );
			idToSegment.put( key, imageSegment );
		}
	}

	@Override
	public T getImageSegment( LabelFrameAndImage labelFrameAndImage )
	{
		return idToSegment.get( labelFrameAndImage );
	}

	@Override
	public String getName()
	{
		return modelName;
	}
}
