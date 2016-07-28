package com.aihomework.voicedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihomework.games.fiveChess.game.Coordinate;
import com.aihomework.games.fiveChess.game.Game;
import com.aihomework.games.fiveChess.game.GameConstants;
import com.aihomework.games.fiveChess.game.GameView;
import com.aihomework.games.fiveChess.game.Player;
import com.aihomework.games.fiveChess.game.RobotAI;

/**
 * 人机对战
 */
public class RobotGameActivity extends Activity implements OnClickListener {
    private GameView mGameView;

    private Game mGame;
    private Player me;
    private Player computer;
    private RobotAI ai;
    // 胜局
    private TextView mBlackWin;
    private TextView mWhiteWin;
    // 当前落子方
    private ImageView mBlackActive;
    private ImageView mWhiteActive;
    // 姓名
    private TextView mBlackName;
    private TextView mWhiteName;
    // Control Button
    private Button restart;
    private Button rollback;

    ImageView figureImage = null;

    private boolean isRollback;

    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mComputerHandler;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //remove title bar
        setContentView(R.layout.game_single);
        initViews();
        initGame();
        initComputer();
    }


    private void initViews() {

        mGameView = (GameView) findViewById(R.id.game_view);
        mBlackName = (TextView) findViewById(R.id.black_name);
        mBlackWin = (TextView) findViewById(R.id.black_win);
        mBlackActive = (ImageView) findViewById(R.id.black_active);
        mWhiteName = (TextView) findViewById(R.id.white_name);
        mWhiteWin = (TextView) findViewById(R.id.white_win);
        mWhiteActive = (ImageView) findViewById(R.id.white_active);
        restart = (Button) findViewById(R.id.restart);
        rollback = (Button) findViewById(R.id.rollback);

        figureImage = (ImageView)findViewById(R.id.figureView);
        figureImage.setBackgroundResource(R.drawable.frame);

        restart.setOnClickListener(this);
        rollback.setOnClickListener(this);
    }

    private void initGame() {
        me = new Player(getString(R.string.myself), Game.BLACK);
        computer = new Player(getString(R.string.computer), Game.WHITE);
        mGame = new Game(mRefreshHandler, me, computer);
        mGame.setMode(GameConstants.MODE_SINGLE);
        mGameView.setGame(mGame);
        updateActive(mGame);
        updateScore(me, computer);
        ai = new RobotAI(mGame.getWidth(), mGame.getHeight());
    }

    private void initComputer() {
        HandlerThread thread = new HandlerThread("computerAi");
        thread.start();
        mComputerHandler = new ComputerHandler(thread.getLooper());
    }

    private void updateActive(Game game) {
        if (game.getActive() == Game.BLACK) {
            mBlackActive.setVisibility(View.VISIBLE);
            mWhiteActive.setVisibility(View.INVISIBLE);
        } else {
            mBlackActive.setVisibility(View.INVISIBLE);
            mWhiteActive.setVisibility(View.VISIBLE);
        }
    }

    private void updateScore(Player black, Player white) {
        mBlackWin.setText(black.getWin());
        mWhiteWin.setText(white.getWin());
    }


    /**
     * 处理游戏回调信息，刷新界面
     */
    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GameConstants.GAME_OVER:
                    if (msg.arg1 == Game.BLACK) {
                        showWinDialog("黑方胜！");
                        me.win();
                    } else if (msg.arg1 == Game.WHITE) {
                        showWinDialog("白方胜！");
                        computer.win();
                    }
                    updateScore(me, computer);
                    break;
                case GameConstants.ACTIVE_CHANGE:
                    updateActive(mGame);
                    break;
                case GameConstants.ADD_CHESS:
                    updateActive(mGame);
                    if (mGame.getActive() == computer.getType()) {
                        mComputerHandler.sendEmptyMessage(0);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showWinDialog(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setMessage(message);
        b.setPositiveButton("继续", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGame.reset();
                mGameView.drawGame();
            }
        });
        b.setNegativeButton("不玩了", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(RobotGameActivity.this,EntranceActivity.class));
                mComputerHandler.getLooper().quit();
                System.exit(0);
            }
        });
        b.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                mGame.reset();
                updateActive(mGame);
                updateScore(me, computer);
                mGameView.drawGame();
                break;
            case R.id.rollback:
                if (mGame.getActive() != me.getType()) {
                    isRollback = true;
                } else {
                    rollback();
                }
                break;
            default:
                break;
        }

    }

    private void rollback() {
        mGame.rollback();
        mGame.rollback();
        updateActive(mGame);
        mGameView.drawGame();
    }

    class ComputerHandler extends Handler {

        public ComputerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            ai.updateValue(mGame.getChessMap());
            Coordinate c = ai.getPosition(mGame.getChessMap());
            mGame.addChess(c, computer);
            mGameView.drawGame();
            if (isRollback) {
                rollback();
                isRollback = false;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mComputerHandler.getLooper().quit();
        startActivity(new Intent(RobotGameActivity.this,EntranceActivity.class));
        System.exit(0);
    }
}