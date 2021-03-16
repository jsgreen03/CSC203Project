import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.*;

class AStarPathingStrategy implements PathingStrategy {


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        // this is the path for the moving entity to take
        List<Point> path = new LinkedList<Point>();

        // using a priority queue since we want to grab the element with the lowest f value
        // use a comparator to see which value is higher for f
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparing(Node::getF));
        // open list, use hashmap to see where points are
        HashMap<Point, Node> openHM = new HashMap<>();
        // closed list
        HashMap<Point, Node> closedHM = new HashMap<>();

        // starting point/ node
        // pass in the starting point for the intial node
        Node startN = new Node(null, start);
        //pass in the ending point for the final node (and we don't know yet what the prior node is)
        Node endN = new Node(null, end);

        openList.add(startN);
        openHM.put(start, startN);
        boolean stop = false;

        // keep checking until we reach the end and empty the priority queue and the node isn't found
        while (!openList.isEmpty() && !withinReach.test(startN.getPoint(), end) && !(stop)) {

            //grab all the valid neighbor nodes
            List<Point> neighborP = potentialNeighbors.apply(startN.getPoint()).filter(canPassThrough).filter(pt -> !closedHM.containsKey(pt)).collect(Collectors.toList());

            // go through all the neighbor nodes
            for (Point p : neighborP) {
                // make a node with the neighbor point
                Node n = new Node(startN, p);
                int gn = startN.getG() + 1;
                n.setG(gn);
                // manhattan distance
                int hn = (Math.abs(p.x - endN.current.x) + Math.abs(p.y - endN.current.y));
                n.setH(hn);
                n.setF();

                // if its not in closed list add it to open list
                if (!closedHM.containsKey(n.getPoint()) && !openHM.containsKey((n.getPoint()))) {
                    openHM.put(n.getPoint(), n);
                    openList.add(n);
                }
                // check if point is already in hash map and if new g value is bigger than old g value
                if (openHM.containsKey(p) && (n.getG() < openHM.get(p).getG())) {
                    // if g is bigger than replace the old node with the new node with the bigger g value
                    openHM.put(p, n);
                    openHM.remove(p);
                    openList.add(n);
                    openList.remove(openHM.get(p));
                }
            }

            // remove current node from open queue and map
            openList.remove(startN);
            openHM.remove(startN.getPoint());
            // move current node to the closed list
            closedHM.put(startN.getPoint(), startN);

            if (openList.peek() != null) {
                // sort the nodes in order of f value
                startN = openList.poll();
            }
            if (openList.isEmpty()) {
                // if there's no path
                return path;
            }
        }

        // add the points to the path
        Node pathN = startN;
        while (pathN.getPoint() != start) {
            path.add(0, pathN.getPoint());
            pathN = pathN.getPrior();
        }

        return path;
    }



    private class Node {
        private Node prior;
        private Point current;
        private int g;
        private int h;
        private int f;

        public Node(Node prior, Point current) {
            this.prior = prior;
            this.current = current;
            // may set g and h to 0
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        public int getF() {
            return f;
        }

        public Point getPoint() {
            return current;
        }

        public int getG() {
            return g;
        }

        public void setG(int g){
            this.g = g;
        }

        public void setH(int h){
            this.h = h;
        }

        public void setF(){
            this.f = g + h;
        }

        public Node getPrior(){
            return prior;
        }


    }



}

