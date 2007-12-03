/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.core.utils;

/**
 * The Class LEDistance.
 */
public class LEDistance {

    /** The COM p_ LEN. */
    private static int compLen = 100;

    /** The AR r_ SIZE. */
    private static int arrSize = compLen + 1;

    /** The ACCEPTED. */
    private static boolean accepted = true;

    /** The REJECTED. */
    private static boolean rejected = false;

    // These next values may be adjusted based on the language used, and
    // other considerations. The values assigned below seem to work well
    // with English.

    /** The ADDITION. */
    private static int addition = 1;

    /** The CHANGE. */
    private static int chnger = 2;

    /** The DELETION. */
    private static int deletion = 1;

    /**
     * Checks if is alike.
     * 
     * @param requested
     *        the requested
     * @param found
     *        the found
     * 
     * @return true, if is alike
     */
    public static boolean isAlike(final String requested, final String found) {
        int i;
        int j;
        int rLen;
        int fLen;
        int threshold;
        int[][] distance = new int[arrSize][arrSize];

        if (toupper(requested.charAt(0)) != toupper(found.charAt(0))) return rejected;

        // If a found string starts with the requested string,
        // return true.
        // Disable if inappropriate for specific applications.
        // (An addition to the original C code.)

        if (found.length() > requested.length()) {
            if (found.toLowerCase().startsWith(requested.toLowerCase())) return accepted;
        }

        rLen = (requested.length() > compLen ? compLen : requested.length());

        fLen = (found.length() > compLen ? compLen : found.length());

        // A minimum threshold of three is used for better results
        // with short strings
        // (A modification to the original C code.)

        threshold = Math.max(3, (int) Math.floor(1 + (rLen + 2) / 4.0));

        if (Math.abs(rLen - fLen) > threshold) return rejected;

        distance[0][0] = 0;

        for (j = 1; j < arrSize; j++) {
            distance[0][j] = distance[0][j - 1] + addition;
        }

        for (j = 1; j < arrSize; j++) {
            distance[j][0] = distance[j - 1][0] + deletion;
        }

        for (i = 1; i <= rLen; i++) {
            for (j = 1; j <= fLen; j++) {

                distance[i][j] =
                        smallestOf(distance[i - 1][j - 1]
                                + ((toupper(requested.charAt(i - 1)) == toupper(found.charAt(j - 1))) ? 0 : chnger),
                                distance[i][j - 1] + addition, distance[i - 1][j] + deletion);
            }
        }

        if (distance[rLen][fLen] <= threshold)
            return accepted;
        else
            return rejected;
    }

    /**
     * Smallest of.
     * 
     * @param x
     *        the x
     * @param y
     *        the y
     * @param z
     *        the z
     * 
     * @return the int
     */
    private static int smallestOf(final int x, final int y, final int z) {
        return ((x < y) ? Math.min(x, z) : Math.min(y, z));
    }

    /**
     * Toupper.
     * 
     * @param ch
     *        the ch
     * 
     * @return the char
     */
    private static char toupper(final char ch) {
        if ((ch >= 'a') && (ch <= 'z'))
            return (char) (ch - 32);
        else
            return ch;
    }
}
