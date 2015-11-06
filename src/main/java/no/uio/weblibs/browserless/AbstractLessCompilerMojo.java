package no.uio.weblibs.browserless;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

/*
 * Copyright (c) 2015, University of Oslo, Norway All rights reserved.
 *
 * This file is part of "UiO Software Information Inventory".
 *
 * "UiO Software Information Inventory" is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * "UiO Software Information Inventory" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with "UiO Software Information Inventory". If
 * not, see <http://www.gnu.org/licenses/>
 */

public abstract class AbstractLessCompilerMojo extends AbstractMojo {

    /** @component */
    protected BuildContext buildContext;

    /**
     * The source directory containing the LESS sources.
     *
     * @parameter property="browserless.sourceDirectory" default-value="${project.basedir}/src/main/less"
     * @required
     */
    protected File sourceDirectory;

    /**
     * List of files to include. Specified as fileset patterns which are relative to the source directory. Default value
     * is: { "**\/*.less" }
     *
     * @parameter
     */
    protected String[] includes = new String[] { "**/*.less" };

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the source directory.
     *
     * @parameter
     */
    protected String[] excludes = new String[] {};


    /**
     * Scans for the LESS sources that should be compiled.
     *
     * @return The list of LESS sources.
     */
    protected String[] getIncludedFiles() {
        Scanner scanner = buildContext.newScanner(sourceDirectory, true);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    /**
     * Whether to skip plugin execution.
     * This makes the build more controllable from profiles.
     *
     * @parameter property="browserless.skip" default-value="false"
     */
    protected boolean skip;

}
