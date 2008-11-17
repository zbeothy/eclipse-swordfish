/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Volodymyr Zhabiuk - initial implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.planner;

import java.util.Comparator;

import org.eclipse.swordfish.api.Strategy;

public class StrategyComparator implements Comparator<Strategy> {

	public int compare(Strategy s1, Strategy s2) {
		return (int) Math.signum(s1.getPriority() - s2.getPriority());
	}

}
