/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AAPolygon {

    public class Point {

        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private List<Point> points;

    public AAPolygon() {
        this.points = new ArrayList<Point>();
    }

    public AAPolygon(Point[] points) {
        this();
        this.points.addAll(Arrays.asList(points));
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    public boolean isComplete() {

        // Impossible to make Axis Aligned Polygon with less than 4 points
        if (points.size() < 4) {
            return false;
        }

        Point first = points.get(0);
        Point last = points.get(points.size() - 1);
        Point prev = points.get(points.size() - 2);

        boolean isLinear = (first.x == last.x) || (first.y == last.y);

        // The first and last points don't line up on either axis (diagonal end)
        if (!isLinear) {
            return false;
        }

        int axis_last = (first.x == last.x) ? 1 : 0;
        int axis_prev = (last.x == prev.x) ? 1 : 0;

        // Laast segment is right angle, last remaining fail condition only applies to straight lines
        if (axis_last != axis_prev) {
            return true;
        }

        boolean pos_axis_last = false;
        boolean pos_axis_prev = false;
        switch (axis_last) {
            case 0:
                pos_axis_last = first.y > last.y;
                pos_axis_prev = last.y > prev.y;
                break;
            case 1:
                pos_axis_last = first.x > last.x;
                pos_axis_prev = last.x > prev.x;
                break;
        }

        return pos_axis_last == pos_axis_prev;
    }

    public boolean contains(int x, int y) {
        return contains(new Point(x, y));
    }

    public boolean contains(Point p) {
        boolean inside = false;

        for (int i = 1; i <= points.size(); i++) {
            Point p1 = points.get(i - 1); // From 0 to (points.size()-1)
            Point p2 = points.get(i % points.size()); // From 1 to (points.size()-1) and then to 0

            if (p1.x != p2.x) {
                continue; // Only check vertical lines
            }
            if (p.x > p1.x) {
                continue; // If point is past the line in positive direction
            }
            if (p.y < fastMin(p1.y, p2.y)) {
                continue; // If below the line minimum
            }
            if (p.y > fastMax(p1.y, p2.y)) {
                continue; // If above the line maximum
            }
            inside = !inside;
        }
        return inside;
    }

    private int fastMin(int t1, int t2) {
        return t1 < t2 ? t1 : t2;
    }

    private int fastMax(int t1, int t2) {
        return t1 > t2 ? t1 : t2;
    }
}
