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
package de.embl.cba.tables.github;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public abstract class GitHubUtils
{
	public static GitLocation rawUrlToGitLocation( String rawUrl )
	{
		final GitLocation gitLocation = new GitLocation();
		final String[] split = rawUrl.split( "/" );
		final String user = split[ 3 ];
		final String repo = split[ 4 ];
		gitLocation.branch = split[ 5 ];
		gitLocation.repoUrl = "https://github.com/" + user + "/" + repo;
		gitLocation.path = "";
		for ( int i = 6; i < split.length; i++ )
		{
			gitLocation.path += split[ i ] + "/";
		}
		return gitLocation;
	}

	public static ArrayList< String > getFilePaths( GitLocation gitLocation )
	{
		final GitHubContentGetter contentGetter = new GitHubContentGetter( gitLocation.repoUrl, gitLocation.path, gitLocation.branch, null );
		final String json = contentGetter.getContent();

		GsonBuilder builder = new GsonBuilder();

		final ArrayList< String > bookmarkPaths = new ArrayList<>();
		ArrayList< LinkedTreeMap > linkedTreeMaps = ( ArrayList< LinkedTreeMap >) builder.create().fromJson( json, Object.class );
		for ( LinkedTreeMap linkedTreeMap : linkedTreeMaps )
		{
			final String downloadUrl = ( String ) linkedTreeMap.get( "download_url" );
			bookmarkPaths.add( downloadUrl );
		}
		return bookmarkPaths;
	}
}
