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
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.analysis.c;

import java.io.Reader;
import org.opengrok.analysis.FileAnalyzer;
import org.opengrok.analysis.FileAnalyzerFactory;
import org.opengrok.analysis.JFlexTokenizer;
import org.opengrok.analysis.JFlexXref;
import org.opengrok.analysis.plain.AbstractSourceCodeAnalyzer;

/**
 * An Analyzer for C++ files
 *
 * @author Trond Norbye
 */
public class CxxAnalyzer extends AbstractSourceCodeAnalyzer {

    /**
     * Creates a new instance of {@link CxxAnalyzer}.
     * @param factory defined instance for the analyzer
     */
    protected CxxAnalyzer(FileAnalyzerFactory factory) {
        super(factory, new JFlexTokenizer(new CxxSymbolTokenizer(
            FileAnalyzer.dummyReader)));
    }      

    /**
     * Creates a wrapped {@link CxxXref} instance.
     * @param reader the data to produce xref for
     * @return a defined instance
     */
    @Override
    protected JFlexXref newXref(Reader reader) {
        return new JFlexXref(new CxxXref(reader));
    }
    
    @Override
    protected boolean supportsScopes() {
        return true;
    }
}
