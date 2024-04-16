package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Functions {
    public static void drawScaled(SpriteBatch spriteBatch, Texture texture,float scale,float x,float y)
    {
        spriteBatch.draw(new TextureRegion(texture),x,y,0,0,texture.getWidth(),texture.getHeight(),scale,scale,0);
    }

    public static void drawScaled(SpriteBatch spriteBatch,Texture texture,float scale,float x,float y,float rotationDegrees)
    {
        spriteBatch.draw(new TextureRegion(texture),x,y,0,0,texture.getWidth(),texture.getHeight(),scale,scale, MathUtils.degreesToRadians*rotationDegrees);
    }

    public static void drawRotated(SpriteBatch spriteBatch,Texture texture,float x,float y,float rotationDegrees)
    {
        spriteBatch.draw(new TextureRegion(texture),x-texture.getWidth()/2,y-texture.getHeight()/2, texture.getWidth() /2,texture.getHeight()/2,texture.getWidth(),texture.getHeight(),1,1,rotationDegrees);
    }

    public static void drawRotatedScaled(SpriteBatch spriteBatch,Texture texture,float x,float y,float rotationDegrees,float scale)
    {
        spriteBatch.draw(new TextureRegion(texture),x-texture.getWidth()/2,y-texture.getHeight()/2, texture.getWidth() /2,texture.getHeight()/2,texture.getWidth(),texture.getHeight(),scale,scale,rotationDegrees);
    }

    public static float rotateTowards(float fromAngle,float xFrom,float yFrom,float xTo,float yTo,float correctionAngle,float rotationSpeed)
    {
        return MathUtils.lerpAngle(fromAngle,MathUtils.atan2(yTo-yFrom,xTo-xFrom)+correctionAngle,rotationSpeed);
    }

    public static boolean validTarget(Ship target,Ship forShip)
    {
        return !target.isLanded() && target.getCurrentSystem()==forShip.getCurrentSystem() && target.getIntegrity()>0;
    }

    public static float rotationTowards(float xFrom,float yFrom,float xTo,float yTo,float correctionAngle)
    {
        return MathUtils.atan2(yTo-yFrom,xTo-xFrom)+correctionAngle;
    }

    public static Vector2 intercept(Vector2 src,Vector2 dstPos,Vector2 dstVelocity,float v)
    {
        float tx=dstPos.x-src.x;
        float ty=dstPos.y-src.y;
        float tvx=dstVelocity.x;
        float tvy=dstVelocity.y;
        float a=tvx*tvx+tvy*tvy-v*v;
        float b=2*(tvx*tx*tvy*ty);
        float c=tx*tx+ty*ty;

        float[] ts=quad(a,b,c);
        Vector2 sol=null;
        if(ts!=null)
        {
            float t0=ts[0];
            float t1=ts[1];
            float t=Math.min(t0,t1);
            if(t<0)
                t=Math.max(t0,t1);
            if(t>0)
            {
                sol=new Vector2(dstPos.x+dstVelocity.x*t,dstPos.y+dstVelocity.y*t);
            }
        }
        return sol;
    }

    private static float[] quad(float a, float b, float c)
    {
        float[] sol;
        if(Math.abs(a)<1e-6)
        {
            if(Math.abs(b)<1e-6){
                sol=Math.abs(c)<1e-6 ? new float[]{0,0}:null;
            }
            else {
                sol=new float[]{-c/b,-c/b};
            }
        }else {
            float disc=b*b-4*a*c;
            a=2*a;
            sol=new float[]{(-b-disc)/a,(-b+disc)/2};
        }
        return sol;
    }

    public static void log(String message)
    {
        Gdx.app.log("Info",message);
    }

    public static boolean validTarget(Ship ship,StarSystem sourceSystem)
    {
        return !ship.isLanded() && ship.getIntegrity() > 0 && sourceSystem==ship.getCurrentSystem();
    }
}
