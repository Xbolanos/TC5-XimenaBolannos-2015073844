package cr.ac.itcr.andreifuentes.flappybirdclase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Random;



import static com.badlogic.gdx.Input.Keys.R;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture[] birds;
	Texture gameOver;
	Texture easy;
	Texture medium;
	Texture hard;

	int birdState;
	float gap;
	float birdY;
	float velocity;
	float gravity;
	int numberOfPipes = 4;
	float pipeX[] = new float[numberOfPipes];
	float pipeYOffset[] = new float[numberOfPipes];
	float distance;
	float pipeVelocity = 5;
	Random random;
	float maxLine;
	float minLine;
	int score;
	int pipeActivo;
	BitmapFont font;
	int game_state;
	int birdSize;
	float rotation;
	TextureRegion region;
	int soundfailflag;
	Circle birdCircle;
	Rectangle[] topPipes;
	Rectangle[] bottomPipes;
	Sound soundfail;
	Sound soundpoint;
	Sound sound;

	private Stage stage;
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		stage = new Stage(new ScreenViewport());
		easy=new Texture("easy.png");
		medium=new Texture("medium.png");
		hard=new Texture("hard.png");

		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");





		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameOverOriginal.png");
		soundfail = Gdx.audio.newSound(Gdx.files.internal("fail.mp3"));
		sound = Gdx.audio.newSound(Gdx.files.internal("fly.mp3"));
		soundpoint = Gdx.audio.newSound(Gdx.files.internal("win.mp3"));
		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];


		birdState = 0;

		region = new TextureRegion(birds[birdState],0,0,512,512);
		region.flip(true, true);
		game_state = 3;
		gap = 500;
		velocity = 0;
		gravity = 0.5f;
		rotation=-90;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		score = 0;
		pipeActivo = 0;
		birdSize = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[birdState].getHeight()/2;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() + Gdx.graphics.getWidth() + distance*i;

			// inicializamos cada uno de los Shapes
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();

		batch.draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		// no iniciado
		if (game_state == 0){
			if (Gdx.input.justTouched()){
				game_state = 1;
			}
		}
		// jugando
		else if (game_state == 1){
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				score++;
				long idpoint = soundpoint.play();
				soundpoint.setVolume(idpoint, 1f);




				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}


			birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[birdState].getHeight()/2, birds[birdState].getWidth()/2);

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.MAGENTA);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//

			// Posicionamiento de los pipes
			for (int i = 0; i<numberOfPipes; i++) {

				if (pipeX[i] < -topTube.getWidth()){
					pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
					pipeX[i] += distance*(numberOfPipes);
				}
				else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topTube,
						pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				batch.draw(bottomTube,
						pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				bottomPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

//				shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube.getWidth(),
//						bottomTube.getHeight());

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");
					game_state = 2;
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");
					game_state = 2;
				}
			}

			if (Gdx.input.justTouched()){

				long id1 = sound.play();
				sound.setVolume(id1, 1f);

				velocity = velocity - 15;
				rotation=rotation+20;
			}

			birdState = birdState == 0 ? 1 : 0;


			velocity = velocity + gravity;
			rotation=rotation - (gravity+velocity);

			if (birdY < 0){
				game_state = 2;
			}
			else {
				birdY = birdY - velocity;
			}

//			shapeRenderer.end();


		}
		// game over
		else if (game_state == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			long id;

			if(soundfailflag==0) {
				soundfailflag=1;
				id = soundfail.play();
				soundfail.setVolume(id, 1f);
			}else{

			}
			if (Gdx.input.justTouched()){

				soundfailflag=0;
				game_state = 3;
				score = 0;
				pipeActivo = 0;
				velocity = 1;


			}
		}
		else if (game_state == 3){
			batch.draw(easy, Gdx.graphics.getWidth()/2 - easy.getWidth()/2, Gdx.graphics.getHeight()/5 * 4 - easy.getHeight()/2);
			batch.draw(medium, Gdx.graphics.getWidth()/2 - medium.getWidth()/2, Gdx.graphics.getHeight()/5 * 3 - medium.getHeight()/2);
			batch.draw(hard, Gdx.graphics.getWidth()/2 - hard.getWidth()/2, Gdx.graphics.getHeight()/5 * 2 - hard.getHeight()/2);



			if (Gdx.input.justTouched()){
				int xeasy=Gdx.graphics.getWidth()/2 - easy.getWidth()/2;
				int yeasy=Gdx.graphics.getHeight()/5  - easy.getHeight()/2;

				int xmedium=Gdx.graphics.getWidth()/2 - medium.getWidth()/2;
				int ymedium=Gdx.graphics.getHeight()/5 * 2 - medium.getHeight()/2;

				int xhard=Gdx.graphics.getWidth()/2 - hard.getWidth()/2;
				int yhard=Gdx.graphics.getHeight()/5 *3 - hard.getHeight()/2;

				Vector3 tmp=new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);




				Rectangle textureBounds=new Rectangle(Gdx.graphics.getWidth()/2 - easy.getWidth()/2,Gdx.graphics.getHeight()/5 * 4 - easy.getHeight()/2, easy.getWidth(), easy.getHeight());
				// texture x is the x position of the texture
				// texture y is the y position of the texture
				// texturewidth is the width of the texture (you can get it with texture.getWidth() or textureRegion.getRegionWidth() if you have a texture region
				// textureheight is the height of the texture (you can get it with texture.getHeight() or textureRegion.getRegionhHeight() if you have a texture region

				if (Gdx.input.getX() > xeasy && Gdx.input.getX()< xeasy+ easy.getWidth() && Gdx.input.getY() > yeasy && Gdx.input.getY()< yeasy+ easy.getHeight()) {
					Gdx.app.log("LOGRADO","SI");
					gap = 500;
					velocity = 0;
					gravity = 0.5f;
					game_state = 1;
					startGame();
				}else{
					if (Gdx.input.getX() > xmedium && Gdx.input.getX()< xmedium+ medium.getWidth() && Gdx.input.getY() > ymedium && Gdx.input.getY()< ymedium+ medium.getHeight()) {
						Gdx.app.log("LOGRADO","SI/");
						gap = 400;
						velocity = 1;
						gravity = 1f;
						game_state = 1;
						startGame();
					}else{
						if (Gdx.input.getX() > xhard && Gdx.input.getX()< xhard+ hard.getWidth() && Gdx.input.getY() > yhard && Gdx.input.getY()< yhard+ hard.getHeight()) {
							Gdx.app.log("LOGRADO","SI//");
							gap = 300;
							velocity = 2;
							gravity = 1.5f;
							game_state = 1;
							startGame();
						}
					}
				}


			}
		}

		float originy=birds[birdState].getWidth()/2;
		float originx=birds[birdState].getHeight()/2;
	    batch.draw(new TextureRegion(birds[birdState]), Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2,  birdY,
				originx,originy
				,birds[birdState].getWidth(),
				birds[birdState].getHeight(),0.7f,1.3f, rotation, false);
		font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/8, Gdx.graphics.getHeight()*9/10);

		batch.end();


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		soundfail.dispose();
		soundpoint.dispose();

		sound.dispose();
	}
}
