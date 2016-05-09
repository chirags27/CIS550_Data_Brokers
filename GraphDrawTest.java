import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class GraphDrawTest
{
    public static void main(String[] args)
    {
        Graph graphdraw = new Graph("Output");

        graphdraw.setSize(300,300);
        
        graphdraw.setVisible(true);

        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
        map.put("123456", new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5")));
        map.put("ddddddddddddddd", new ArrayList<String>(Arrays.asList("6", "7", "8", "9", "10")));
        map.put("123456xxxxx", new ArrayList<String>(Arrays.asList("11", "12", "13", "14", "15")));
        map.put("1234qqq", new ArrayList<String>(Arrays.asList("16", "17", "18", "19", "20")));
        map.put("12", new ArrayList<String>(Arrays.asList("21", "22", "23", "24", "25")));
        map.put("1234", new ArrayList<String>(Arrays.asList("26", "27", "28", "29", "30")));
        map.put("123456xxxxxxxxxx", new ArrayList<String>(Arrays.asList("31", "32", "33", "34", "35")));

        fetchData(graphdraw, map);
    }

    static void fetchData(Graph graphdraw, LinkedHashMap<String, ArrayList<String>> map)
    {
        Set set = map.entrySet();
        Iterator i = set.iterator();
        int width = 100;
        int height = 100;
        int ii = 0;

        while(i.hasNext())
        {
           Map.Entry me = (Map.Entry)i.next();
           graphdraw.addNode("" + me.getKey(), width, height, "red");
           width += 130;
           height += 52;
        }

        Iterator j = set.iterator();
        while(j.hasNext())
        {
            Map.Entry me = (Map.Entry) j.next();
            if(j.hasNext())
                graphdraw.addEdge(ii, ii + 1, "red");
            ii++;
        }

        int jj = 0, iter;
        width = 100; height = 100;
        ArrayList<String> list;

        for(String key : map.keySet())
        {
            list = map.get(key);
            iter = 0;
            for (String s : list)
            {
                if(iter == 0)
                    graphdraw.addNode(s, width, height + 90, "black");
                else if(iter == 1)
                    graphdraw.addNode(s, width, height - 90, "black");
                else if(iter == 2)
                    graphdraw.addNode(s, width + 90, height - 90, "black");
                else if(iter == 3)
                    graphdraw.addNode(s, width - 90, height + 90, "black");
                else if(iter == 4)
                    graphdraw.addNode(s, width + 90, height, "black");
                graphdraw.addEdge(jj, ii, "black");
                ii++; iter++;
            }
            jj++;
            width += 130;
            height += 52;
        }
    }
}
