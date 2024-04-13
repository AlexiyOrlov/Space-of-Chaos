package dev.buildtool;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;

import java.util.ArrayList;
import java.util.Random;

public class SpaceOfChaos extends Game {
	static Random random=new Random();
	SpriteBatch worldBatch;
	SpriteBatch uiBatch;
	ArrayList<Texture> planetTextures;
	AssetManager assetManager;
	ArrayList<StarSystem> starSystems;
	ArrayList<Texture> starTextures;
	public PlayerShip playerShip;
	Texture redStarshipTexture;
	public Texture scalesTexture,takeOffTexture,slotTexture,alcoholTexture,toolsTexture,carPartsTexture,firearmsTexture,
			jewelleryTexture, clothesTexture,electronicsTexture,waterTexture,furnitureTexture,foodTexture,medicineTexture,
		basicProjectile,ironOreTexture,copperOreTexture,diamondTexture,droneTexture1,gearTexture,slotTexture2,starIcon,
		targetTexture,skyTexture, tradingHull1Texture,starGateTexture,inhabitedPlanetIcon,uninhabitedPlanetIcon,
		battleHull2,pelletTexture,stargateIcon,battleHull3,pirateHull1,engine1Texture,engine2Texture,engine3Texture,shipIcon,
		ship2icon,slotTexture3,thrusters1Texture,thrusters2Texture,gunTexture,shotgunTexture,cashTexture,drone2Texture,
		containerTexture,shipIcon3,blackHullTexture,blackHull2Texture,tradingHull2Texture,pirateHull2Texture,pirateHull3Texture,
		basicGunTexture,machineGunTexture,battleHull3Texture,aiSmallHull1,aiSmallHull2,aiMediumHull1,aiMediumHull2,aiBigHull1,
			aiBigHull2,aiLargeHull1,aiLargeHull2,redProjectileTexture,clusterGunTexture;
	public static SpaceOfChaos INSTANCE;
	public Skin skin;
	ShapeRenderer shapeRenderer,uiShapeRenderer;
	public BitmapFont bitmapFont;
	public GlyphLayout textMeasurer;
	public boolean updateWorld;
	static boolean debugDraw;
	public Sound machineGunSound,laserShotSound,blasterSound,shotGunSound;
	private final ArrayList<Texture> textures=new ArrayList<>(600);
	private final ArrayList<Sound> sounds=new ArrayList<>(100);
	
	@Override
	public void create () {
		INSTANCE=this;
		textMeasurer=new GlyphLayout();
		bitmapFont=new BitmapFont();
		bitmapFont.getData().markupEnabled=true;
		assetManager=new AssetManager();
		worldBatch = new SpriteBatch();
		uiBatch=new SpriteBatch();
		shapeRenderer=new ShapeRenderer();
		uiShapeRenderer=new ShapeRenderer();
		loadTexture("star");
		loadTexture("green planet");
		loadTexture("greenish planet");
		loadTexture("mixed planet");
		loadTexture("planet3");
		loadTexture("planet5");
		loadTexture("red ship");
		loadTexture("scales64");
		loadTexture("take off");
		loadTexture("blue square");
		loadTexture("projectile 1");
		loadTexture("iron ore");
		loadTexture("copper ore");
		loadTexture("resource");
		loadTexture("drone 1");
		loadTexture("gears64");
		loadTexture("slot");
		loadTexture("star icon");
		loadTexture("target_indicator");
		loadTexture("trade hull 1");
		loadTexture("battle hull 2");
		loadTexture("pellet");
		loadTexture("cash64");

		loadTexture("alcohol");
		loadTexture("tools");
		loadTexture("car parts");
		loadTexture("firearms");
		loadTexture("jewellery");
		loadTexture("clothes");
		loadTexture("electronics");
		loadTexture("water bottle");
		loadTexture("furniture");
		loadTexture("food");
		loadTexture("medicine");
		loadTexture("sky");
		loadTexture("star gate");
		loadTexture("inhabited planet");
		loadTexture("uninhabited planet");
		loadTexture("star gate icon");
		loadTexture("battle hull 1");
		loadTexture("pirate hull 1");
		loadTexture("engine1");
		loadTexture("engine2");
		loadTexture("engine3");
		loadTexture("ship icon");
		loadTexture("ship icon2");
		loadTexture("slot 2");
		loadTexture("thrusters 1");
		loadTexture("thrusters 2");
		loadTexture("gun");
		loadTexture("shotgun");
		loadTexture("drone 1");
		loadTexture("container");
		loadTexture("ship icon2");
		loadTexture("hull2");
		loadTexture("bumblebee hull");
		loadTexture("trading hull2");
		loadTexture("pirate hull2");
		loadTexture("pirate hull3");
		loadTexture("basic gun");
		loadTexture("machine gun");
		loadTexture("battle hull 3");
		loadTexture("ai big hull 1");
		loadTexture("ai big hull 2");
		loadTexture("ai hull small 2");
		loadTexture("ai hull small 1");
		loadTexture("ai large hull 1");
		loadTexture("ai large hull 2");
		loadTexture("ai medium hull 2");
		loadTexture("ai medium hull 1");
		loadTexture("projectile2");
		loadTexture("cluster gun");
		assetManager.finishLoading();

		alcoholTexture=getTexture("alcohol");
		toolsTexture=getTexture("tools");
		carPartsTexture=getTexture("car parts");
		firearmsTexture=getTexture("firearms");
		jewelleryTexture=getTexture("jewellery");
		clothesTexture=getTexture("clothes");
		electronicsTexture=getTexture("electronics");
		waterTexture=getTexture("water bottle");
		furnitureTexture=getTexture("furniture");
		foodTexture=getTexture("food");
		medicineTexture=getTexture("medicine");

		basicProjectile=getTexture("projectile 1");
		copperOreTexture=getTexture("copper ore");
		ironOreTexture=getTexture("iron ore");
		diamondTexture=getTexture("resource");
		droneTexture1=getTexture("drone 1");
		gearTexture=getTexture("gears64");
		slotTexture2=getTexture("slot");
		starIcon=getTexture("star icon");
		targetTexture=getTexture("target_indicator");
		skyTexture=getTexture("sky");
		skyTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		tradingHull1Texture=getTexture("trade hull 1");
		starGateTexture=getTexture("star gate");
		battleHull2=getTexture("battle hull 2");
		pelletTexture=getTexture("pellet");
		engine1Texture=getTexture("engine1");
		engine2Texture=getTexture("engine2");
		engine3Texture=getTexture("engine3");
		slotTexture3=getTexture("slot 2");
		thrusters1Texture=getTexture("thrusters 1");
		thrusters2Texture=getTexture("thrusters 2");
		gunTexture=getTexture("gun");
		shotgunTexture=getTexture("shotgun");
		cashTexture=getTexture("cash64");
		drone2Texture=getTexture("drone 1");
		containerTexture=getTexture("container");
		shipIcon3=getTexture("ship icon2");
		blackHullTexture=getTexture("hull2");
		blackHull2Texture=getTexture("bumblebee hull");
		pirateHull3Texture=getTexture("pirate hull3");
		basicGunTexture=getTexture("basic gun");
		machineGunTexture=getTexture("machine gun");

		starTextures=new ArrayList<>(3);
		starTextures.add(getTexture("star"));
		planetTextures=new ArrayList<>();
		planetTextures.add(getTexture("green planet"));
		planetTextures.add(getTexture("greenish planet"));
		planetTextures.add(getTexture("mixed planet"));
		planetTextures.add(getTexture("planet3"));
		planetTextures.add(getTexture("planet5"));
		inhabitedPlanetIcon=getTexture("inhabited planet");
		uninhabitedPlanetIcon=getTexture("uninhabited planet");
		stargateIcon=getTexture("star gate icon");
		battleHull3=getTexture("battle hull 1");
		pirateHull1=getTexture("pirate hull 1");
		shipIcon=getTexture("ship icon");
		ship2icon=getTexture("ship icon2");
		tradingHull2Texture=getTexture("trading hull2");
		pirateHull2Texture=getTexture("pirate hull2");
		battleHull3Texture=getTexture("battle hull 3");

		takeOffTexture=getTexture("take off");
		scalesTexture=getTexture("scales64");
		redStarshipTexture=getTexture("red ship");
		slotTexture=getTexture("blue square");
		redProjectileTexture=getTexture("projectile2");
		clusterGunTexture= getTexture("cluster gun");

		aiSmallHull1=getTexture("ai hull small 1");
		aiSmallHull2=getTexture("ai hull small 2");
		aiMediumHull1=getTexture("ai medium hull 1");
		aiMediumHull2=getTexture("ai medium hull 2");
		aiBigHull1=getTexture("ai big hull 1");
		aiBigHull2=getTexture("ai big hull 2");
		aiLargeHull1=getTexture("ai large hull 1");
		aiLargeHull2=getTexture("ai large hull 2");
		skin=new Skin(Gdx.files.internal("skins/cloud/cloud-form-ui.json"));
		skin.get("font", BitmapFont.class).getData().markupEnabled=true;
		setScreen(new StartScreen(this));
		VisUI.load();

		machineGunSound=loadSound("machine gun.wav");
		laserShotSound=loadSound("laser-shot.wav");
		blasterSound=loadSound("retro-shot-blaster.wav");
		shotGunSound=loadSound("shotgun-spas.mp3");
	}

	private Sound loadSound(String soundName)
	{
		Sound sound=Gdx.audio.newSound(Gdx.files.internal("sounds/"+soundName));
		sounds.add(sound);
		return sound;
	}

	@Override
	public void dispose () {
		worldBatch.dispose();
		skin.dispose();
		uiBatch.dispose();
		bitmapFont.dispose();
		textures.forEach(Texture::dispose);
		sounds.forEach(Sound::dispose);
		VisUI.dispose();
	}

	public void initialize()
	{
		starSystems=new ArrayList<>();
		int x=0;
		int y=0;
		int planetCount=0;
		int starSystemCount=30;
		for (int i = 0; i <starSystemCount; i++) {
			int xleft=random.nextInt(-300,-100);
			int xright=random.nextInt(100,300);
			int yleft=random.nextInt(-300,-100);
			int yright=random.nextInt(100,300);
			while (true) {
				boolean recalculate=false;
				for (StarSystem starSystem : starSystems) {
					if (Vector2.dst(starSystem.positionX, starSystem.positionY, x, y) < 100) {
						if (random.nextBoolean())
							x += xleft;
						else
							x += xright;
						if (random.nextBoolean())
							y += yleft;
						else
							y += yright;
						recalculate=true;
					}
				}
				if(!recalculate)
					break;
			}
			StarSystem starSystem=new StarSystem(planetTextures,starTextures,x,y, i>starSystemCount/2);
			starSystems.add(starSystem);
			planetCount+=starSystem.planets.size();
		}
		System.out.println("Generated "+planetCount+ " planets in total");
		updateWorld=false;
	}

	@Override
	public void render() {
		super.render();
		if(updateWorld)
			starSystems.forEach(StarSystem::update);
		if(Gdx.input.isKeyJustPressed(Input.Keys.F3))
		{
			debugDraw=!debugDraw;
		}
	}

	private void loadTexture(String name)
	{
		assetManager.load("textures/"+name+".png", Texture.class);
	}

	private Texture getTexture(String name)
	{
		Texture texture = assetManager.get("textures/" + name + ".png");
		textures.add(texture);
		return texture;
	}

	public static int getWindowHeight()
	{
		return Gdx.graphics.getHeight();
	}
}
