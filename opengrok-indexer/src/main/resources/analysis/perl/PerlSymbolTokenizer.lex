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
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */

/*
 * Gets Perl symbols - ignores comments, strings, keywords
 */

package org.opengrok.analysis.perl;

import java.io.IOException;
import java.util.regex.Pattern;
import org.opengrok.analysis.JFlexSymbolMatcher;
import org.opengrok.util.StringUtils;
import org.opengrok.web.HtmlConsts;
%%
%public
%class PerlSymbolTokenizer
%extends JFlexSymbolMatcher
%implements PerlLexer
%unicode
%int
%char
%init{
    h = new PerlLexHelper(QUO, QUOxN, QUOxL, QUOxLxN, this,
        HERE, HERExN, HEREin, HEREinxN, SCOMMENT, POD);
    yyline = 1;
%init}
%include CommonLexer.lexh
%{
    private final PerlLexHelper h;

    private String lastSymbol;

    /**
     * Resets the Perl tracked state; {@inheritDoc}
     */
    public void reset() {
        super.reset();
        h.reset();
        lastSymbol = null;
    }

    @Override
    public void offer(String value) throws IOException {
        // noop
    }

    @Override
    public boolean offerSymbol(String value, int captureOffset,
        boolean ignoreKwd)
            throws IOException {
        if (ignoreKwd || !Consts.kwd.contains(value)) {
            lastSymbol = value;
            onSymbolMatched(value, yychar + captureOffset);
            return true;
        } else {
            lastSymbol = null;
        }
        return false;
    }

    @Override
    public void skipSymbol() {
        lastSymbol = null;
    }

    @Override
    public void offerKeyword(String value) throws IOException {
        lastSymbol = null;
    }

    @Override
    public void startNewLine() throws IOException {
        // noop
    }

    @Override
    public void disjointSpan(String className) throws IOException {
        // noop
    }

    @Override
    public void phLOC() {
        // noop
    }

    @Override
    public void abortQuote() throws IOException {
        yypop();
        if (h.areModifiersOK()) yypush(QM);
        disjointSpan(null);
    }

    // If the state is YYINITIAL, then transitions to INTRA; otherwise does
    // nothing, because other transitions would have saved the state.
    public void maybeIntraState() {
        if (yystate() == YYINITIAL) yybegin(INTRA);
    }

    protected boolean takeAllContent() {
        return false;
    }

    protected boolean returnOnSymbol() {
        return lastSymbol != null;
    }

    protected void skipLink(String url, Pattern p) {
        int n = StringUtils.countPushback(url, p);
        if (n > 0) yypushback(n);
    }
%}

%include Common.lexh
%include CommonURI.lexh
%include CommonPath.lexh
%include PerlProductions.lexh
