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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssFileUriRelativizer {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("url\\(['\"]?(file:(.+?))['\"]?\\)");

    private final Path root;


    public CssFileUriRelativizer(final Path root) {
        this.root = root;
    }


    public String relativizeCss(final String css) {
        StringBuilder sb = new StringBuilder(css);

        List<UriRef> refs = getFileUriRefsInCss(css);

        // Iterate backwards to preserve indices after replace
        for (int i = refs.size() - 1; i >= 0; --i) {
            UriRef ref = refs.get(i);
            Path relative = root.relativize(Paths.get(ref.uri));
            sb.replace(ref.startIdx, ref.endIdx, relative.toString());
        }

        return sb.toString();
    }


    private List<UriRef> getFileUriRefsInCss(final String css) {
        Matcher m = FILE_URI_PATTERN.matcher(css);
        List<UriRef> refs = new ArrayList<>();
        while (m.find()) {
            String uri = m.group(2);
            int startIdx = m.start(1);
            int endIdx = m.end(1);
            refs.add(new UriRef(startIdx, endIdx, uri));
        }
        return refs;
    }

    private static class UriRef {
        final int startIdx;
        final int endIdx;
        final String uri;


        UriRef(final int start, final int end, final String uri) {
            this.startIdx = start;
            this.endIdx = end;
            this.uri = uri;
        }
    }

}
