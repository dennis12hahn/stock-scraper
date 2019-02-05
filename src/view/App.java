package view;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.StockReader;

public class App extends Application {

	private static final BorderPane bp = new BorderPane();
	private static final HBox buttons = new HBox();
	private static final Button all = new Button("Downlad All");
	private static final Button screen = new Button("Download Screen");
	private static final Button excel = new Button("Export to Excel");
	private static final Button load = new Button("Load");
	private static final Button save = new Button("Save");
	private static final Label label = new Label();
	private static final FileChooser fc = new FileChooser();
	private StockReader stockReader;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() {
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files (*.*)", "*.*"));

		buttons.getChildren().addAll(all, screen, excel, save, load);
		buttons.setSpacing(5);
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(5));

		label.setPadding(new Insets(5));
		label.setAlignment(Pos.CENTER);
		label.setFont(new Font("Trebuchet MS", 20));

		bp.setCenter(buttons);
		bp.setBottom(label);
		BorderPane.setAlignment(label, Pos.CENTER);
	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(bp, 600, 100);

		primaryStage.setTitle("StockReader");
		primaryStage.setScene(scene);
		primaryStage.show();

		all.setOnAction(e -> {
			stockReader = new StockReader();
			updateLabel("Downloading");
			new Thread(() -> {
				toggleButtons(buttons, true);
				stockReader.importAll();
				completeTask();
			}).start();
		});

		screen.setOnAction(e -> {
			stockReader = new StockReader();
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Screened URL");
			dialog.setHeaderText("Enter screened URL");
			String url = dialog.showAndWait().get();
			updateLabel("Downloading");
			new Thread(() -> {
				toggleButtons(buttons, true);
				stockReader.importScreen(url);
				completeTask();
			}).start();
		});

		excel.setOnAction(e -> {
			fc.getExtensionFilters().remove(0);
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));
			File file = fc.showSaveDialog(primaryStage);
			if (file != null) {
				updateLabel("Exporting");
				new Thread(() -> {
					toggleButtons(buttons, true);
					stockReader.exportToExcel(file.getAbsolutePath());
					completeTask();
				}).start();
			}
		});

		load.setOnAction(e -> {
			stockReader = new StockReader();
			fc.getExtensionFilters().remove(0);
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("StockReader (*.stocks)", "*.stocks"));
			File file = fc.showOpenDialog(primaryStage);
			if (file != null) {
				updateLabel("Loading");
				new Thread(() -> {
					toggleButtons(buttons, true);
					stockReader.load(file.getAbsolutePath());
					completeTask();
				}).start();
			}
		});

		save.setOnAction(e -> {
			fc.getExtensionFilters().remove(0);
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("StockReader (*.stocks)", "*.stocks"));
			File file = fc.showSaveDialog(primaryStage);
			if (file != null) {
				updateLabel("Saving");
				new Thread(() -> {
					toggleButtons(buttons, true);
					stockReader.save(file.getAbsolutePath());
					completeTask();
				}).start();
			}
		});

		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
		});
	}

	private static void completeTask() {
		toggleButtons(buttons, false);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				label.setVisible(false);
			}

		});
	}

	private static void toggleButtons(HBox hbox, boolean bool) {
		for (int i = 0; i < hbox.getChildren().size(); i++) {
			hbox.getChildren().get(i).setDisable(bool);
		}
	}

	private static void updateLabel(String str) {
		label.setVisible(true);
		Task<Void> dynamicText = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				String dots = new String();
				while (true) {
					if (dots.length() == 3) {
						dots = "";
					} else {
						dots += ".";
					}
					updateMessage(str + dots);
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {
						break;
					}
				}
				return null;
			}
		};
		label.textProperty().bind(dynamicText.messageProperty());
		Thread t2 = new Thread(dynamicText);
		t2.setDaemon(true);
		t2.start();
	}
}
