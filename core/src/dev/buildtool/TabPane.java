package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TabPane extends Table {
    private final Table contentTable;
    private final Table tabTable;
    private int tabCount;
    public TabPane(Skin skin) {
        super(skin);
        contentTable=new Table();
        tabTable=new Table();
        add(tabTable);
        row();
        add(contentTable);
    }

    public void addTab(Table content,String name)
    {
        TextImageButton textImageButton=new TextImageButton(name,getSkin(),null);
        textImageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                 contentTable.clear();
                 contentTable.add(content);
            }
        });
        tabTable.add(textImageButton);
        if(tabCount==0)
        {
            contentTable.add(content);
        }
        tabCount++;
    }
}
