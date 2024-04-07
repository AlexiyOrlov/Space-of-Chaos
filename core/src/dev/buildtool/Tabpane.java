package dev.buildtool;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class Tabpane {
    ArrayList<Table> pane;

    public Tabpane(int tabCOunt) {
        pane=new ArrayList<>(tabCOunt);
    }

    public void showTab(int i)
    {
        pane.get(i).setVisible(true);
    }

    public void showTab(Table tab)
    {
        for (Table table : pane) {
            if(tab==table)
            {
                tab.setVisible(true);
                break;
            }
        }
    }

    public void addTab(Table table)
    {
        pane.add(table);
    }
}
