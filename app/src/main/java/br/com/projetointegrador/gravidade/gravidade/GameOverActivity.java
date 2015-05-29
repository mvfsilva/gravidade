package br.com.projetointegrador.gravidade.gravidade;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

public class GameOverActivity extends SimpleBaseGameActivity{
        public static int CAMERA_WIDTH  = 480;
        public static int CAMERA_HEIGHT = 800;

        private Scene scene = new Scene();
        private Camera camera;

        private Sprite GameOverSprite;
        private ITextureRegion GameOverTextureRegion;

        private Long pontos = 0L;
        private Long recorde = 0L;
        private BitmapTextureAtlas pontuacaoTextureAtlas;
        private Font pontuacaoFont;
        private Text textoPontuacao;

        private Sprite botaoInativoSprite;
        private BitmapTextureAtlas texBotaoInativo;
        private TiledTextureRegion botaoRegiaoInativo;
        private float mDowX, mDowY;

        @Override
        protected void onCreate(Bundle pSavedInstanceState) {
            if (this.getIntent() != null && this.getIntent().getExtras() != null) {
                Bundle bundle = this.getIntent().getExtras();
                pontos = bundle.getLong("pontos");
                recorde = bundle.getLong("recorde");
            }
            super.onCreate(pSavedInstanceState);
        }

        @Override
        public EngineOptions onCreateEngineOptions() {
            //Captura tamanho da tela
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int height = metrics.heightPixels;
            int width  = metrics.widthPixels;

            if (CAMERA_HEIGHT < height && CAMERA_WIDTH < width) {
                CAMERA_HEIGHT = height;
                CAMERA_WIDTH = width;
            }

            camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

            //Engine options
            EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), camera);
            engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
            engineOptions.getRenderOptions().setDithering(true);
            engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
            engineOptions.getTouchOptions().setNeedsMultiTouch(true);

            return engineOptions;
        }

        @Override
        protected void onCreateResources() throws IOException {
           try{
               ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
                   @Override
                   public InputStream open() throws IOException {
                       return getAssets().open("newgameover.png");
                   }
               });

               this.GameOverTextureRegion= TextureRegionFactory.extractFromTexture(backgroundTexture, 0, 0, 400, 494);

               texBotaoInativo = new BitmapTextureAtlas(this.getTextureManager(),342,64, TextureOptions.DEFAULT);
               this.botaoRegiaoInativo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                       texBotaoInativo, this.getAssets(), "botao_inativo.png", 0, 0, 1, 1
               );

               pontuacaoTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),256, 256,
                       TextureOptions.BILINEAR_PREMULTIPLYALPHA);
               this.pontuacaoFont = new Font(this.getFontManager(),pontuacaoTextureAtlas, Typeface.create(
                       Typeface.DEFAULT, Typeface.BOLD), 35, true, Color.WHITE);
               this.mEngine.getTextureManager().loadTexture(pontuacaoTextureAtlas);
               this.mEngine.getFontManager().loadFont(pontuacaoFont);

               texBotaoInativo.load();
               backgroundTexture.load();
           }catch (IOException e) {
               Debug.e(e);
           }

        }

        @Override
        protected Scene onCreateScene() {
            this.GameOverSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.GameOverTextureRegion, this.getVertexBufferObjectManager());
            GameOverSprite.setWidth(CAMERA_WIDTH);
            GameOverSprite.setHeight(CAMERA_HEIGHT);
            scene.attachChild(this.GameOverSprite);

            this.botaoInativoSprite = new Sprite(this.CAMERA_WIDTH/2,this.CAMERA_HEIGHT - 400
                    ,this.botaoRegiaoInativo,this.getVertexBufferObjectManager()){
                //Cria o touch dentro do botao
                @Override
                public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                                             float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    int eventAction = pSceneTouchEvent.getAction();
                    float X = pSceneTouchEvent.getX();
                    float Y = pSceneTouchEvent.getY();

                    switch (eventAction) {
                        case TouchEvent.ACTION_DOWN:
                            Log.e("Fudeu", X + "" + Y);
                            mDowX = X;
                            mDowY = Y;
                            break;

                        case TouchEvent.ACTION_MOVE:
                            break;

                        case TouchEvent.ACTION_UP:
                            Log.e("Fudeu","UP "+X+""+Y);
                            restartMainActivity();
                            break;
                    }
                    return true;
                }
            };
            scene.attachChild(this.botaoInativoSprite);
            scene.registerTouchArea(this.botaoInativoSprite);

            // Pontos
            String textoPontos = "Pontuação: " + pontos + " / Recorde: " + recorde;
            float centroXpontos = (this.CAMERA_WIDTH / 2);/* - (this.pontuacaoFont
                    .getStringWidth(textoPontos) / 2);*/
            float centroYpontos = (this.CAMERA_HEIGHT - 100) - (this.pontuacaoFont
                    .getLineHeight() / 2);
            textoPontuacao = new Text(centroXpontos, centroYpontos,
                    this.pontuacaoFont, textoPontos, this.getVertexBufferObjectManager());
            scene.attachChild(textoPontuacao);

            return scene;
        }

    private void restartMainActivity(){
        Intent playGame = new Intent(this, MainActivity.class);
        startActivity(playGame);
        this.finish();
    }
 }