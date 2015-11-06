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

package no.uio.weblibs.browserless;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * Goal which compiles the LESS sources to CSS stylesheets.
 *
 * @author Julian Ravn Thrap-Meyer
 * @goal compile
 * @phase process-sources
 */
public class BrowserLessCompilerMojo extends AbstractLessCompilerMojo {

    /**
     * The directory for compiled CSS stylesheets.
     *
     * @parameter property="browserless.outputDirectory" default-value="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * When <code>true</code> the LESS compiler will compress the CSS stylesheets.
     *
     * @parameter property="browserless.compress" default-value="false"
     */
    private boolean compress;

    /**
     * The character encoding the LESS compiler will use for writing the CSS stylesheets.
     *
     * @parameter property="browserless.encoding" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    /**
     * The location of the LESS JavasSript file.
     *
     * @parameter
     */
    private File lessJs;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping plugin execution");
            return;
        }

        String[] includedFiles = getIncludedFiles();

        if (getLog().isDebugEnabled()) {
            getLog().debug("[outputDirectory=" + outputDirectory +
                    ", sourceDirectory=" + sourceDirectory +
                    ", lessJs=" + lessJs +
                    ", compress=" + compress +
                    ", encoding=" + encoding +
                    ", files=" + Arrays.toString(includedFiles) +
                    "]");
        }

        WebDriver driver = getDriver();

        LessCompiler less = new LessCompiler(lessJs.toPath(), driver);
        less.setCompress(compress);
        less.setEncoding(encoding);

        for (String fileName : includedFiles) {
            try {
                compileLessFile(less, fileName);
            } catch (Exception e) {
                getLog().error("Error compiling less files:" + e, e);
                e.printStackTrace();
            }
        }

        driver.close();
    }


    private void compileLessFile(final LessCompiler less, final String fileName) throws IOException,
            MojoExecutionException {
        Path file = sourceDirectory.toPath().resolve(fileName);
        getLog().info("Compiling less file: " + file);
        long start = System.currentTimeMillis();

        String css = less.compile(file);
        Path target = getTargetPath(fileName);
        writeContentToFile(css, target);

        long end = System.currentTimeMillis();
        getLog().info("Compiled to " + target + " in " + (end - start) + "ms");
    }


    private void writeContentToFile(final String content, final Path path) throws IOException {
        Files.write(path, content.getBytes(encoding), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }


    private Path getTargetPath(final String fileName) throws MojoExecutionException {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new MojoExecutionException("Cannot create output directory " + outputDirectory);
        }

        String compiledName = fileName.replace(".less", ".css");
        return outputDirectory.toPath().resolve(compiledName);
    }


    private WebDriver getDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.setJavascriptEnabled(true);
        return driver;
    }

}
