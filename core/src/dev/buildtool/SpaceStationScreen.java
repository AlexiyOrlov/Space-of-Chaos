package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class SpaceStationScreen extends ScreenAdapter implements StackHandler {
    private SpaceStation spaceStation;
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final ArrayList<SlotButton> slotButtons=new ArrayList<>();

    private final Label moneyLabel;
    public SpaceStationScreen(SpaceStation spaceStation,PlayerShip playerShip) {
        SpaceOfChaos.INSTANCE.updateWorld=false;
        this.spaceStation = spaceStation;
        viewport=new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        viewport.apply();
        stage=new Stage(viewport);
        Skin skin=SpaceOfChaos.INSTANCE.skin;
        moneyLabel=new Label(""+playerShip.money,skin);
    }
    @Override
    public Stack getStackUnderMouse() {
        return stackUnderMouse;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        stackUnderMouse=stack;
    }
}
