package socialnetwork.helpers;

import socialnetwork.domain.User;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    Repository<Long, User> repo;
    Byte[][] graph;

    public Graph(Repository<Long, User> repo) {
        this.repo = repo;
        getGraph();
    }

    /**
     * generates current graph using initializeMatrix()
     */
    private void getGraph() {
        initializeMatrix();
        for (User user : repo.findAll()) {
            for (Long friendId : user.getFriends()) {
                graph[(byte) getCorespondent(user.getId())][(byte) getCorespondent(friendId)] = 1;
            }
        }
    }

    /**
     *
     * @param id of user
     * @return number of node that represents given id
     * if repo stores all users in order by id and all id's are consecutive number starting from 0, then
     *      all ids are identical to their corespondent nodes
     */
    private long getCorespondent(long id){
        int index=0;
        for (User user: repo.findAll())
            if (user.getId()==id)
                break;
            else
                index++;
        return index;
    }

    /**
     * generates a matrix only with 0 values
     */
    private void initializeMatrix() {
        int sizeOfRepo = getNoNodes();
        graph = new Byte[sizeOfRepo][sizeOfRepo];

        for (Byte[] row : graph)
            Arrays.fill(row, (byte) 0);

        // TODO: cum as face asta cu List in loc de vector raw
//        graph=new ArrayList<>(sizeOfRepo);
//
//        List<Byte> row=new ArrayList<>(sizeOfRepo);
//        // TODO: cum as scrie asta cu lambda
////        repo.findAll().forEach(x->{row.add(0)});
//
//        for (int i=0;i<sizeOfRepo;i++){
//            row.add((byte)0);
//        }
//
//        for (int i=0;i<sizeOfRepo;i++){
//            graph.add(row);
//        }
    }

    /**
     *
     * @return number of nodes is size of repo (no users)
     */
    private int getNoNodes() {
        int sizeOfRepo = 0;
        // TODO: cum as scrie asta cum lambda in .foreach
        for (User el : repo.findAll()) {
            sizeOfRepo++;
        }
        return sizeOfRepo;
    }

    /**
     *
     * @return number of conex components / communities
     */
    public int getNoCommunities() {
        // TODO: check how to initialize an arrayList with values
        int noNodes = getNoNodes();
        Byte[] visited = new Byte[noNodes];
        Arrays.fill(visited, (byte) 0);
        int noCommunities = 0;
        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == 0) {
                DFS(i, visited);
                noCommunities++;
            }
        }
        return noCommunities;
    }


    /**
     * marks nodes that can be accesed starting from startingNode
     * @param startNode - starting node for DFS
     * @param visited - array of size number of nodes
     *                visited[i]=1  -> (user with corespondent) node i can be accesed starting from startNode
     *                visited[i]=0      ,else
     */
    private void DFS(int startNode, Byte[] visited) {
        for (int i = 0; i < visited.length; i++)
            if (visited[i] == 0 && (graph[startNode][i] == 1 || i == startNode)) {
                visited[i] = 1;
                DFS(i, visited);
            }
    }

    private int maxSize=-1;
    private int startingNodeForLongestPath=-1;
    //   TODO: vezi daca poti face asta variabila locala
    /**
     * returns max length of a path starting from start node
     * length is defined by number of unique edges traveled
     * @param startNode - starting node for DFS (input)
     * @param size - current number of edges traveled (input)
     * @param visited -  matrix with values 0,1,2 with noNodes lines and noNodes columns (output)
     *                matrix[i][j]=matrix[j][i]=0   , there is no edge from i to j and from j to i
     *                matrix[i][j]=matrix[j][i]=1   , there is edge from i to j and from j to i and it was not traveled/visited
     *                matrix[i][j]=matrix[j][i]=2   , there is edge from i to j and from j to i and it was traveled/visited
     */
    private void DFS2(int startNode, int size, Byte[][] visited) {
        for (int i = 0; i < visited.length; i++)
            if (visited[startNode][i] == 1 && graph[startNode][i] == 1) {
                visited[startNode][i] = 2;
                visited[i][startNode] = 2;
                Byte[][] copyOfVisited=copyVisited(visited);
                DFS2(i, size + 1, copyOfVisited);
            }
        if (size>maxSize)
            startingNodeForLongestPath=startNode;
        maxSize=Math.max(maxSize,size);
    }

    /**
     * @return list with all users that are part of the friendliest community
     */
    public List<Long> getFriendliestCommunity(){
        getFriendliestCommunitySize();
        int noNodes=getNoNodes();
        Byte[] visited=new Byte[noNodes];
        Arrays.fill(visited,(byte)0);
        DFS(startingNodeForLongestPath,visited);
        Iterable<User> users=repo.findAll();
        int index=0;
        List<Long> result=new ArrayList<>();
        for (User user : users){
            if (visited[index]==1)
                result.add(user.getId());
            index++;
        }
        return result;
    }

    /**
     *
     * @param matrix - 2 dimensions array
     * @return deep copy of matrix
     */
    private Byte[][] copyVisited(Byte[][] matrix){
        Byte[][] visitedAdjacencyMatrix = matrix.clone();    // create deep copy of graph
        for (int i=0;i<visitedAdjacencyMatrix.length;i++)
            visitedAdjacencyMatrix[i]=matrix[i].clone();
        return visitedAdjacencyMatrix;
    }

    /**
     *
     * @return size of the longest elementary path in the graph
     */
    public int getFriendliestCommunitySize() {
//        Byte[][] listaMuchii=getListaMuchii();
//        int noNodes=getSizeOfRepo();
//        DFS2(startNode,noNodes,0);
        int noNodes = getNoNodes();

        Byte[][] visitedAdjacencyMatrix = graph.clone();    // create deep copy of graph
        for (int i=0;i<visitedAdjacencyMatrix.length;i++)
            visitedAdjacencyMatrix[i]=graph[i].clone();

        int maxLength = -1;
        int nodeWithMaxPath = -1;
        for (int i = 0; i < noNodes; i++) {
            DFS2(i, 0, visitedAdjacencyMatrix);
            int dfsValue=maxSize;
            maxLength = Math.max(maxLength, dfsValue);
        }

        return maxLength;
    }

}
