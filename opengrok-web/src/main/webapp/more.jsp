<%--
$Id$

CDDL HEADER START

The contents of this file are subject to the terms of the
Common Development and Distribution License (the "License").
You may not use this file except in compliance with the License.

See LICENSE.txt included in this distribution for the specific
language governing permissions and limitations under the License.

When distributing Covered Code, include this CDDL HEADER in each
file and include the License file at LICENSE.txt.
If applicable, add the following below this CDDL HEADER, with the
fields enclosed by brackets "[]" replaced with your own identifying
information: Portions Copyright [yyyy] [name of copyright owner]

CDDL HEADER END

Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
Portions Copyright 2011 Jens Elkner.
Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.

--%><%@page errorPage="error.jsp" import="
java.io.FileInputStream,
java.io.Reader,
java.nio.charset.StandardCharsets,
java.util.logging.Level,
java.util.logging.Logger,

org.apache.lucene.search.IndexSearcher,
org.apache.lucene.search.Query,
org.opengrok.configuration.RuntimeEnvironment,
org.opengrok.search.QueryBuilder,
org.opengrok.search.context.Context,
org.opengrok.logger.LoggerFactory,
org.opengrok.util.IOUtils,
org.opengrok.web.SearchHelper"
%>
<%
{
    PageConfig cfg = PageConfig.get(request);
    cfg.checkSourceRootExistence();
}
%><%@include

file="mast.jsp"

%><%
/* ---------------------- more.jsp start --------------------- */
{
    PageConfig cfg = PageConfig.get(request);
    File resourceFile = cfg.getResourceFile();
    String path = cfg.getPath();
    RuntimeEnvironment env = cfg.getEnv();
    Project activeProject = Project.getProject(resourceFile);

    QueryBuilder qbuilder = null;
    SearchHelper searchHelper = null;
    int docId = -1;
    int tabSize = 0;

    if (activeProject == null) {
        qbuilder = cfg.getQueryBuilder();
    } else {
        searchHelper = cfg.prepareInternalSearch();
        /*
         * N.b. searchHelper.destroy() is called via
         * WebappListener.requestDestroyed() on presence of the following
         * REQUEST_ATTR.
         */
        request.setAttribute(SearchHelper.REQUEST_ATTR, searchHelper);
        searchHelper.prepareExec(activeProject);
        if (searchHelper.searcher != null) {
            docId = searchHelper.searchSingle(resourceFile);
            qbuilder = searchHelper.builder;
            searchHelper.prepareSummary();
            tabSize = searchHelper.getTabSize(activeProject);
        }
    }

    try {
        Query tquery = qbuilder.build();
        if (tquery != null) {
%><p><span class="pagetitle">Lines Matching <b><%= tquery %></b></span></p>
<div id="more" style="line-height:1.5em;">
    <pre><%
            String xrefPrefix = request.getContextPath() + Prefix.XREF_P;
            boolean didPresentNew = false;
            if (docId >= 0) {
                didPresentNew = searchHelper.sourceContext.getContext2(env,
                    searchHelper.searcher, docId, out, xrefPrefix, null, false,
                    tabSize);
            }
            if (!didPresentNew) {
                /**
                 * Fall back to the old view, which re-analyzes text using
                 * PlainLinetokenizer. E.g., when source code is updated (thus
                 * affecting timestamps) but re-indexing is not yet complete.
                 */
                Context sourceContext = new Context(tquery, qbuilder);
                sourceContext.toggleAlt();
                // SRCROOT is read with UTF-8 as a default.
                try (Reader r = IOUtils.createBOMStrippedReader(
                        new FileInputStream(resourceFile),
                        StandardCharsets.UTF_8.name())) {
                    sourceContext.getContext(r, out, xrefPrefix, null, path,
                        null, false, false, null, null);
                }
            }
    %></pre>
</div><%
        }
    } catch (Exception e) {
        LoggerFactory.getLogger(more_jsp.class).log(Level.WARNING, e.getMessage());
    }
}
/* ---------------------- more.jsp end --------------------- */
%><%@

include file="foot.jspf"

%>