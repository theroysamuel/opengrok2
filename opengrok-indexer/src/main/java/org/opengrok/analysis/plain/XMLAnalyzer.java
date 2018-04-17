/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2005, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.analysis.plain;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.lucene.document.Document;
import org.opengrok.analysis.FileAnalyzerFactory;
import org.opengrok.analysis.JFlexXref;
import org.opengrok.analysis.StreamSource;
import org.opengrok.analysis.TextAnalyzer;
import org.opengrok.analysis.OGKTextField;
import org.opengrok.analysis.WriteXrefArgs;
import org.opengrok.analysis.Xrefer;
import org.opengrok.search.QueryBuilder;

/**
 * Analyzes HTML files Created on September 30, 2005
 *
 * @author Chandan
 */
public class XMLAnalyzer extends TextAnalyzer {

    /**
     * Creates a new instance of XMLAnalyzer
     * @param factory factory to be used
     */
    protected XMLAnalyzer(FileAnalyzerFactory factory) {
        super(factory);
    }

    @Override
    public void analyze(Document doc, StreamSource src, Writer xrefOut) throws IOException {
        doc.add(new OGKTextField(QueryBuilder.FULL,
            getReader(src.getStream())));

        if (xrefOut != null) {
            try (Reader in = getReader(src.getStream())) {
                WriteXrefArgs args = new WriteXrefArgs(in, xrefOut);
                args.setProject(project);
                Xrefer xref = writeXref(args);

                addNumLines(doc, xref.getLineNumber());
                addLOC(doc, xref.getLOC());
            }
        }
    }

    /**
     * Creates a wrapped {@link XMLXref} instance.
     * @param reader the data to produce xref for
     * @return an xref instance
     */
    @Override
    protected JFlexXref newXref(Reader reader) {
        return new JFlexXref(new XMLXref(reader));
    }
}
