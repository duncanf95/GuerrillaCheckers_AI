package com.CardboardGames.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.CardboardGames.AI.ADVAgent;
import com.CardboardGames.Listeners.BooVariable;
import com.CardboardGames.R;
import com.CardboardGames.Controllers.GameController;
import com.CardboardGames.Models.BoardModel;
import com.CardboardGames.Views.BoardView;




import java.io.FileReader;
import java.io.InputStream;

public class GuerillaCheckersActivity extends Activity
	implements OnTouchListener
{
	/// PUBLIC METHODS






	public GuerillaCheckersActivity(){
		blisten.setListener(new BooVariable.ChangeListener() {
			public void onChange() {
				Log.d("BOOLCHANGE", "happened");
				if(blisten.getBoo()){
					if(agent.GetAgentChoice() == 'g'){
						Log.d("BOOLCHANGE", "found agent");

						//m_controller.moveMade = false;
						//m_controller.moveToNextState();

						AsyncTask.execute(new Runnable() {
							public void run() {
								//TODO your background code
								//blisten.setBoo(false);
								//agent.makeMove();
								//Log.d("run", "works");
							}
						});

					}else if(agent.GetAgentChoice() == 'c') {
						m_view.invalidate();

						Log.d("BOOLCHANGE", "found agent");


						//m_view.requestLayout();
						//m_view.forceDraw();

						if (m_controller.getState() != "END") {
							loadDialog.show();
							AsyncTask.execute(new Runnable() {
								public void run() {
									//TODO your background code
									blisten.setBoo(false);
									agent.makeMove();

									if (m_model.lastCoinMoveCaptured()) {
										m_model.setCoinMustCapture(true);

										while (m_model.getCoinMustCapture()) {
											Log.d("GAME_TAKE", "found take");

											agent.coinTake();

											if (!(m_model.selectedCoinPieceHasValidMoves())) {
												m_model.setCoinMustCapture(false);
											}
										}
										m_controller.moveToNextState();
									}

									Log.d("run", "works");
								}
							});
							//blisten.setBoo(false);
							//agent.makeMove();
							//m_view.requestLayout();
							m_controller.moveMade = false;
							//m_controller.moveToNextState();
						}
					}
				}
			}
		});

		alisten.setListener(new BooVariable.ChangeListener() {
			public void onChange() {
				if(alisten.getBoo()) {
					Log.d("BOOLCHANGE", "agent");

					m_Handler.post(new Runnable() {
						public void run(){
							loadDialog.hide();
						}
					});
					//loadDialog.show();
					m_view.postInvalidate();
					m_controller.moveToNextState();
					//m_controller.moveToNextState();
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GuerillaCheckersApplication app =
			GuerillaCheckersApplication.getInstance();

		m_model = app.getModel();
		m_view = new BoardView(this, m_model);
		m_view.setDrawUpdated(drawUpdated);
		m_view.setOnTouchListener(this);
		m_controller = app.getController();
		m_controller.setView(m_view);
		agent = app.getAgent();
		m_controller.setAgent(agent);
		agent.setView(m_view);
		agent.alisten= alisten;
		setContentView(m_view);
		LoadingDialogCreate();

		showDialog(DIALOG_CHOOSE_TEAM);

		m_Handler = new Handler();
	}

	public boolean onTouch(View view, MotionEvent event) {
		if (event.getActionMasked() != MotionEvent.ACTION_DOWN)
			return false;
		m_controller.addTouch(event.getX(), event.getY());
		blisten.setBoo(m_controller.moveMade);
		return true;
	}

	private Dialog buildTeamChoiceDialog() {
		String team_names[] = getResources().getStringArray(R.array.team_names);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_choose_team);
		builder.setItems(team_names, m_chooseTeamHandler);
		builder.create();
		return builder.show();
	}

	private Dialog buildrulesdialog() {

		rules Rules = new rules();
		String team_names[] = getResources().getStringArray(R.array.team_names);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Rules");

		builder.setItems(team_names, m_chooseTeamHandler);
		builder.setMessage(Html.fromHtml(Rules.Rules));
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				buildTeamChoiceDialog();
			}
		});
		return builder.create();
	}

	private void LoadingDialogCreate(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Loading");
		builder.setCancelable(true);
		builder.setMessage("please wait");
		loadDialog = builder.create();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == 1){
			id = 0;
		}else{
			id = 1;
		}
	    switch(id) {
	    case DIALOG_CHOOSE_TEAM:
			Log.d("onCreateDialog", Integer.toString(id));
	    	return buildTeamChoiceDialog();
		case DIALOG_EXPLAIN_RULES:
			Log.d("onCreateDialog", Integer.toString(id));
			return buildrulesdialog();
	    default:
	        return super.onCreateDialog(id);
	    }
	}

	/// EVENT HANDLERS

	private final DialogInterface.OnClickListener m_chooseTeamHandler =
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int idx_team) {
				switch (idx_team) {
				case IDX_COIN: // TODO: implement
					Log.d("onClick", "1");
					m_model.setPlayer('c');
					Log.d("onClick", "2");
					m_controller.setupGuerilla();
					Log.d("onClick", "3");
					m_view.invalidate();
					Log.d("onClick", "4");
					agent.makeMove();
					Log.d("onClick", "5");
					m_view.invalidate();
					Log.d("onClick", "6");
					agent.makeMove();
					break;
				case IDX_GUERILLA: // TODO: implement
					m_model.setPlayer('g');
					break;
				default:
					assert(false);
				}
			}
		};

	/// PRIVATE MEMBERS
	private static final int DIALOG_EXPLAIN_RULES =0;
	private static final int DIALOG_CHOOSE_TEAM = 1;
	private static final int IDX_COIN = 0;
	private static final int IDX_GUERILLA = 1;
	private boolean moveMade = false;
	private BooVariable blisten = new BooVariable();
	private BooVariable alisten = new BooVariable();
	private boolean drawUpdated = false;

	GameController m_controller = null;
	BoardModel m_model = null;
	BoardView m_view = null;
	AlertDialog loadDialog;
	private ADVAgent agent = null;
	private Handler m_Handler;
}
