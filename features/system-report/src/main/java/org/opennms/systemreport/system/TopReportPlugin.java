/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.systemreport.system;

import java.io.File;
import java.util.TreeMap;

import org.apache.commons.exec.CommandLine;
import org.opennms.systemreport.AbstractSystemReportPlugin;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class TopReportPlugin extends AbstractSystemReportPlugin {
    @Override
    public String getName() {
        return "Top";
    }

    @Override
    public String getDescription() {
        return "Output of the 'top' command (full output only)";
    }

    @Override
    public int getPriority() {
        return 11;
    }

    @Override
    public TreeMap<String, Resource> getEntries() {
        final TreeMap<String,Resource> map = new TreeMap<String,Resource>();

        final String top = findBinary("top");

        String topOutput = null;

        if (top != null) {
            topOutput = slurpOutput(CommandLine.parse(top + " -h"), true);

            if (topOutput.contains("-b") && topOutput.contains("-n")) {
                topOutput = slurpOutput(CommandLine.parse(top + " -n 1 -b"), false);
            } else if (topOutput.contains("-l")) {
                topOutput = slurpOutput(CommandLine.parse(top + " -l 1"), false);
            } else {
                topOutput = null;
            }
        }

        if (topOutput != null) {
            File tempFile = createTemporaryFileFromString(topOutput);
            if(tempFile != null) {
                map.put("Output", new FileSystemResource(tempFile));
            }
        }

        return map;
    }
}
