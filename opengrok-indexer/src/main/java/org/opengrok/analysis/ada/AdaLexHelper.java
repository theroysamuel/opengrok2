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
 * Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */

package org.opengrok.analysis.ada;

import java.io.IOException;
import org.opengrok.analysis.Resettable;
import org.opengrok.analysis.JFlexJointLexer;

/**
 * Represents an API for object's using {@link AdaLexHelper}
 */
interface AdaLexer extends JFlexJointLexer {
}

/**
 * Represents a helper for Ada lexers
 */
class AdaLexHelper implements Resettable {

    private final AdaLexer lexer;

    private final int SCOMMENT;

    public AdaLexHelper(int sCOMMENT, AdaLexer lexer) {
        if (lexer == null) {
            throw new IllegalArgumentException("`lexer' is null");
        }
        this.lexer = lexer;
        this.SCOMMENT = sCOMMENT;
    }

    /**
     * Resets the instance to an initial state.
     */
    @Override
    public void reset() {
        // noop
    }

    /**
     * Write {@code value} to output -- if it contains any EOLs then the
     * {@code startNewLine()} is called in lieu of outputting EOL.
     */
    public void takeLiteral(String value, String className)
            throws IOException {

        lexer.disjointSpan(className);

        int off = 0;
        do {
            int w = 1, ri, ni, i;
            ri = value.indexOf("\r", off);
            ni = value.indexOf("\n", off);
            if (ri == -1 && ni == -1) {
                String sub = value.substring(off);
                lexer.offer(sub);
                break;
            }
            if (ri != -1 && ni != -1) {
                if (ri < ni) {
                    i = ri;
                    if (value.charAt(ri) == '\r' && value.charAt(ni) == '\n') {
                        w = 2;
                    }
                } else {
                    i = ni;
                }
            } else if (ri != -1) {
                i = ri;
            } else {
                i = ni;
            }

            String sub = value.substring(off, i);
            lexer.offer(sub);
            lexer.disjointSpan(null);
            lexer.startNewLine();
            lexer.disjointSpan(className);
            off = i + w;
        } while (off < value.length());

        lexer.disjointSpan(null);
    }

    /**
     * Calls {@link AdaLexer#phLOC()} if the yystate is not SCOMMENT.
     */
    public void chkLOC() {
        if (lexer.yystate() != SCOMMENT) {
            lexer.phLOC();
        }
    }
}
