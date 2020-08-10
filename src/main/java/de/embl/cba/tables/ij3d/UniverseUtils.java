/*-
 * #%L
 * TODO
 * %%
 * Copyright (C) 2018 - 2020 EMBL
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
package de.embl.cba.tables.ij3d;

import bdv.viewer.Source;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Utils;
import de.embl.cba.tables.color.ColorUtils;
import de.embl.cba.util.CopyUtils;
import ij.ImagePlus;
import ij.Prefs;
import ij3d.Content;
import ij3d.Image3DUniverse;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealUnsignedByteConverter;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.vecmath.Color3f;

import java.awt.*;

public class UniverseUtils
{
	public static < R extends RealType< R > > Content addSourceToUniverse(
			Image3DUniverse universe,
			Source< ? > source,
			long maxNumVoxels,
			int displayType,
			ARGBType argbType,
			float transparency,
			int min,
			int max )
	{
		final Integer level = Utils.getLevel( source, maxNumVoxels );

		if ( level == null )
		{
			Logger.warn( "Image is too large to be displayed in 3D." );
			return null;
		}

		if ( universe == null )
		{
			Logger.warn( "No Universe exists => Cannot show volume." );
			return null;
		}

		if ( universe.getWindow() == null )
		{
			Logger.warn( "No Universe window exists => Cannot show volume." );
			return null;
		}


		final ImagePlus wrap = getImagePlus( source, min, max, level );


		final Content content = universe.addContent( wrap, displayType );

		content.setTransparency( transparency );
		content.setLocked( true );
		content.setColor( new Color3f( ColorUtils.getColor( argbType ) ) );

		//segmentToContent.put( segment, content );
		//contentToSegment.put( content, segment );

		universe.setAutoAdjustView( false );

		return content;
	}

	private static < R extends RealType< R > > ImagePlus getImagePlus( Source< ? > source, int min, int max, Integer level )
	{
		RandomAccessibleInterval< ? extends RealType< ? > > rai
				= BdvUtils.getRealTypeNonVolatileRandomAccessibleInterval( source, 0, level );

		rai = CopyUtils.copyVolumeRaiMultiThreaded( ( RandomAccessibleInterval ) rai, Prefs.getThreads() -1  ); // TODO: make multi-threading configurable.

		rai = Views.permute( Views.addDimension( rai, 0, 0 ), 2, 3 );

		final ImagePlus wrap = ImageJFunctions.wrapUnsignedByte(
				( RandomAccessibleInterval ) rai,
				new RealUnsignedByteConverter< R >( min, max ),
				source.getName() );

		final double[] voxelSpacing = Utils.getVoxelSpacings( source ).get( level );
		wrap.getCalibration().pixelWidth = voxelSpacing[ 0 ];
		wrap.getCalibration().pixelHeight = voxelSpacing[ 1 ];
		wrap.getCalibration().pixelDepth = voxelSpacing[ 2 ];

		return wrap;
	}

	public static void addImagePlusToUniverse(
			Image3DUniverse universe,
			ImagePlus imagePlus,
			int displayType,
			double transparency )
	{

		final Content content =
				universe.addContent( imagePlus, displayType );

		content.setTransparency( ( float ) transparency );
		content.setLocked( true );

		//segmentToContent.put( segment, content );
		//contentToSegment.put( content, segment );
	}

	public static void showUniverseWindow( Image3DUniverse universe, Component parentComponent )
	{
		if ( universe.getWindow() == null )
		{
			universe.show();
			universe.getWindow().setResizable( false );

			if ( parentComponent != null )
			{
				universe.getWindow().setPreferredSize( new Dimension(
						parentComponent.getWidth() / 2 ,
						parentComponent.getHeight() / 2  ) );

				universe.getWindow().setLocation(
						parentComponent.getLocationOnScreen().x + parentComponent.getWidth(),
						parentComponent.getLocationOnScreen().y
				);
			}
		}
	}
}
