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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yahoo.platform.yui.compressor.CssCompressor;

public class LessCompiler {

    private final Path lessJsPath;

    private final WebDriver driver;

    private boolean compress = false;

    private String encoding = "UTF-8";


    public LessCompiler(final Path lessJsPath, final WebDriver driver) {
        this.lessJsPath = lessJsPath;
        this.driver = driver;

        if (!Files.exists(lessJsPath)) {
            throw new IllegalArgumentException("less.js not found: " + lessJsPath);
        }
    }


    public void setCompress(final boolean compress) {
        this.compress = compress;
    }


    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }


    public String compile(final Path lessFile) throws IOException {
        String html = getHtmlPageForLessFile(lessFile);

        String css = executeHtmlAndGetCss(html);
        if (compress) {
            css = compressCss(css);
        }
        return css;
    }


    private String compressCss(final String css) throws IOException {
        CssCompressor compressor = new CssCompressor(new StringReader(css));

        StringWriter writer = new StringWriter();
        compressor.compress(writer, -1);
        return writer.toString();
    }


    private String executeHtmlAndGetCss(final String html) throws IOException {
        Path file = writeHtmlToTempFile(html);
        String css = executeFileAndGetCss(file);
        Files.delete(file);

        return css;
    }


    private Path writeHtmlToTempFile(final String html) throws IOException, UnsupportedEncodingException {
        Path file = getNewTempPath();
        return Files.write(file, html.getBytes(encoding), StandardOpenOption.CREATE);
    }


    private Path getNewTempPath() {
        String tempdir = System.getProperty("java.io.tmpdir");
        long now = System.nanoTime();

        Path file = Paths.get(tempdir, "browserless-tmp-" + now + ".html");
        return file;
    }


    private String executeFileAndGetCss(final Path file) {
        driver.get(file.toUri().toString());

        WebElement styleElement = driver.findElement(By.tagName("style"));
        return styleElement.getText();
    }


    private String getHtmlPageForLessFile(final Path lessFile) {
        if (!Files.exists(lessFile)) {
            throw new IllegalArgumentException("Less file not found: " + lessFile);
        }

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<link rel=\"stylesheet/less\" type=\"text/css\" href=\"" + lessFile.toUri() + "\" />"
                + "<script type=\"text/javascript\" src=\"" + lessJsPath.toUri() + "\"></script>"
                + "</head>"
                + "</html>";
    }
}
