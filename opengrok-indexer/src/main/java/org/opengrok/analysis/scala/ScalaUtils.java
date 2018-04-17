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

package org.opengrok.analysis.scala;

import java.util.regex.Pattern;

/**
 * Represents a container for Scala-related utility methods and patterns.
 */
public class ScalaUtils {

    /**
     * Matches a dollar sign, which is a valid URI character but also the start
     * of string interpolation:
     * <pre>
     * {@code
     * \$
     * }
     * </pre>
     * (Edit above and paste below [in NetBeans] for easy String escaping.)
     */
    public static final Pattern DOLLAR_SIGN = Pattern.compile("\\$");

    /** Private to enforce static. */
    private ScalaUtils() {
    }
}
