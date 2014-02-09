package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.dijkstra.Dijkstra;
import cs455.overlay.util.LinkInfo;
import cs455.overlay.util.NodeInfo;

public class DijkstraTest {
    private Dijkstra dike;

    private NodeInfo shavano;
    private NodeInfo kitaro;
    private NodeInfo bierstadt;
    private NodeInfo bismarck;

    private LinkInfo shavano_kitaro;
    private LinkInfo shavano_bismarck;
    private LinkInfo shavano_bierstadt;
    private LinkInfo kitaro_bierstadt;
    private LinkInfo kitaro_bismarck;
    private LinkInfo bierstadt_bismarck;

    @Before
    public void setUp() throws Exception {
        shavano = new NodeInfo("shavano", 63478);
        kitaro = new NodeInfo("kitaro", 62890);
        bierstadt = new NodeInfo("bierstadt", 62839);
        bismarck = new NodeInfo("bismarck", 63890);

        shavano_kitaro = new LinkInfo(shavano, kitaro, 1);
        shavano_bismarck = new LinkInfo(shavano, bismarck, 2);
        shavano_bierstadt = new LinkInfo(shavano, bierstadt, 4);
        kitaro_bierstadt = new LinkInfo(kitaro, bierstadt, 7);
        kitaro_bismarck = new LinkInfo(kitaro, bismarck, 1);
        bierstadt_bismarck = new LinkInfo(bierstadt, bismarck, 9);

        LinkInfo[] links = { shavano_kitaro, shavano_bismarck,
                shavano_bierstadt, kitaro_bierstadt, kitaro_bismarck,
                bierstadt_bismarck };

        dike = new Dijkstra(links);
        dike.setSourceNode(bierstadt);
    }

    @Test
    public void testGetPathTo() {
        // bierstadt - kitaro
        List<NodeInfo> pathToKitaro = dike.getPathTo(kitaro);
        List<NodeInfo> properPath = new ArrayList<NodeInfo>();
        properPath.add(bierstadt);
        properPath.add(shavano);
        properPath.add(kitaro);
        System.out.println("Calculated path, bierstadt -- kitaro: "
            + pathToKitaro);
        assertEquals(properPath, pathToKitaro);

        // bierstadt - shavano
        List<NodeInfo> pathToShavano = dike.getPathTo(shavano);
        properPath.clear();
        properPath.add(bierstadt);
        properPath.add(shavano);
        System.out.println("Calculated path, bierstadt -- shavano: "
            + pathToShavano);
        assertEquals(properPath, pathToShavano);

        // bierstadt - bismarck
        List<NodeInfo> pathToBismarck = dike.getPathTo(bismarck);
        properPath.clear();
        properPath.add(bierstadt);
        properPath.add(shavano);
        properPath.add(bismarck);
        System.out.println("Calculated path, bierstadt -- bismarck: "
            + pathToBismarck);
        assertEquals(properPath, pathToBismarck);
    }

    @Test
    public void testGetPathStringTo() {
        // bierstadt - kitaro
        String pathToKitaro = dike.getPathStringTo(kitaro);

        String properPath = "";
        properPath += bierstadt + " ";
        properPath += shavano_bierstadt.getLinkWeight() + " ";
        properPath += shavano + " ";
        properPath += shavano_kitaro.getLinkWeight() + " ";
        properPath += kitaro + " ";
        System.out.println("Calculated path string, bierstadt -- kitaro");
        System.out.println(pathToKitaro);
        assertEquals(properPath, pathToKitaro);

        // bierstadt - shavano
        String pathToShavano = dike.getPathStringTo(shavano);
        properPath = "";
        properPath += bierstadt + " ";
        properPath += shavano_bierstadt.getLinkWeight() + " ";
        properPath += shavano + " ";
        System.out.println("Calculated path string, bierstadt -- shavano");
        System.out.println(pathToShavano);
        assertEquals(properPath, pathToShavano);

        // bierstadt - bismarck
        String pathToBismarck = dike.getPathStringTo(bismarck);
        properPath = "";
        properPath += bierstadt + " ";
        properPath += shavano_bierstadt.getLinkWeight() + " ";
        properPath += shavano + " ";
        properPath += shavano_bismarck.getLinkWeight() + " ";
        properPath += bismarck + " ";
        System.out.println("Calculated path string, bierstadt -- bismarck");
        System.out.println(pathToBismarck);
        assertEquals(properPath, pathToBismarck);
    }

}
