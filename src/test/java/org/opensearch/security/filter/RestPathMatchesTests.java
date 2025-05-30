/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.security.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class RestPathMatchesTests {
    Method restPathMatches;
    SecurityRestFilter securityRestFilter;

    @Before
    public void setUp() throws NoSuchMethodException {
        securityRestFilter = mock(SecurityRestFilter.class);
        restPathMatches = SecurityRestFilter.class.getDeclaredMethod("restPathMatches", String.class, String.class);
        restPathMatches.setAccessible(true);
    }

    @Test
    public void testExactMatch() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/x/y";
        String handlerPath = "_plugins/security/api/x/y";
        assertTrue((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testPartialMatch() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/x/y";
        String handlerPath = "_plugins/security/api/x/z";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testNamedParamsMatch() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/123/y";
        String handlerPath = "_plugins/security/api/{id}/y";
        assertTrue((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testDifferentPathLength() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/x/y/z";
        String handlerPath = "_plugins/security/api/x/y";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testDifferentPathSegments() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/a/b";
        String handlerPath = "_plugins/security/api/x/y";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testRequestPathWithNamedParam() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "_plugins/security/api/123/y";
        String handlerPath = "_plugins/security/api/{id}/z";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testMatchWithLeadingSlashDifference() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "api/v1/resource";
        String handlerPath = "/api/v1/resource";
        assertTrue((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testMatchWithTrailingSlashDifference() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "/api/v1/resource/";
        String handlerPath = "/api/v1/resource";
        assertTrue((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testPathsMatchWithMultipleNamedParameters() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "/api/v1/resource/123/details";
        String handlerPath = "/api/v1/resource/{id}/details";
        assertTrue((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testPathsDoNotMatchWithNonMatchingNamedParameterSegment() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "/api/v1/resource/123/details";
        String handlerPath = "/api/v1/resource/{id}/summary";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }

    @Test
    public void testDifferentSegmentCount() throws InvocationTargetException, IllegalAccessException {
        String requestPath = "/api/v1/resource/123/extra";
        String handlerPath = "/api/v1/resource/{id}";
        assertFalse((Boolean) restPathMatches.invoke(securityRestFilter, requestPath, handlerPath));
    }
}
