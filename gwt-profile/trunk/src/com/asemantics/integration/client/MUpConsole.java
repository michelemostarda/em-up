/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.integration.client;

import com.asemantics.integration.client.gui.ObjectInspector;
import com.asemantics.integration.client.gui.ContextInspector;
import com.asemantics.integration.client.gui.ContextInspectorListener;
import com.asemantics.mashup.nativepkg.JSFunctions;
import com.asemantics.mashup.nativepkg.NativeException;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.Invocable;
import com.asemantics.mashup.processor.nativeops.ContextOperation;
import com.asemantics.mashup.processor.nativeops.UnifyOperation;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.interpreter.Interpreter;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MUpConsole implements EntryPoint {

    /**
     * Command Entry Panel width.
     */
    final int CEP_WIDTH = 680;

    /**
     * Submit command button listener.
     */
    private class CommandButtonListener implements ClickListener {

        public void onClick(Widget sender) {
            processCommand();
        }
    }

    /**
     * The menu bar.
     */
    private MenuBar menu = new MenuBar();

    /**
     * Decorator panel.
     */
    private DecoratorPanel decoratorPanel = new DecoratorPanel();

    /**
     * The main container.
     */
    private VerticalPanel mainPanel = new VerticalPanel();

    /**
     * Split panel.
     */
    private VerticalSplitPanel splitPanel = new VerticalSplitPanel();

    /**
     * Submission panel.
     */
    private VerticalPanel commandHistoryPanel = new VerticalPanel();

    /**
     * Command panel.
     */
    private HorizontalPanel commandPanel = new HorizontalPanel();

    /**
     * Command text area.
     */
    private TextArea commandArea = new TextArea();

    /**
     * Confirm command button.
     */
    private Button commandButton = new Button();

    /**
     * Status bar panel.
     */
    private DockPanel statusBar = new DockPanel();

    /**
     * Command button listener.
     */
    private CommandButtonListener commandButtonListener = new CommandButtonListener();

    /**
     * Console interpreter.
     */
    private MUpConsoleInterpreter interpreter;

    /**
     * List of history commands.
     */
    private List<String> historyList;

    /**
     * Constructor.
     */
    public MUpConsole() {
        interpreter = new MUpConsoleInterpreter();
        historyList = new LinkedList<String>();
    }

    /**
     * Returns the internal interpreter.
     *
     * @return the console interpreter.
     */
    public Interpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Creates the console layout.
     */
    public void onModuleLoad() {

        // Defines menu.
        menu.setAutoOpen(true);
        menu.setWidth("690px");

        // Console menu.
        MenuBar consoleMenu = new MenuBar(true);

        // Clean console item.
        MenuItem cleanConsole = new MenuItem(
            "Clean Console",
            new Command() {
                public void execute() {
                    clearHistory();
                }
            }
        );
        consoleMenu.addItem(cleanConsole);

        // Show history item.
        MenuItem showHistory = new MenuItem(
            "Show history",
            new Command() {
                public void execute() {
                    showCommandHistory();
                }
            }
        );
        consoleMenu.addItem(showHistory);

        // Get programmative context menu.
        MenuItem getProgrammativeContext = new MenuItem(
            "Programmative Context",
            new Command() {
                public void execute() {
                    try {
                        String programmativecontext = interpreter.getProgrammativeContext();
                        showProgrammativeContextWindow( programmativecontext );
                    } catch (Exception e) {
                        MessageBox.alert( "Error while opening window: " + e.getMessage() );
                    }
                }
            }
        );
        consoleMenu.addItem(getProgrammativeContext);

       // Console menu.
        MenuBar toolsMenu = new MenuBar(true);

        // Show Object inspector.
        MenuItem showOI = new MenuItem(
            "Object Inspector",
            new Command() {
                public void execute() {
                    JsonBase base;
                    try {
                        base = NativeImpl.getInstance().parseJSON("{ 'k1' : 'v1', 'k2' : 'v2'}");
                    } catch (NativeException e) {
                        throw new RuntimeException(e);
                    }
                    ObjectInspector oi = new ObjectInspector(base);
                    oi.show();
                }
            }
        );
        toolsMenu.addItem(showOI);

        // Show Context inspector.
        MenuItem showCI = new MenuItem(
            "Context Inspector",
            new Command() {
                public void execute() {
                    ContextInspector ci = new ContextInspector( getInterpreter() );
                    ci.addListener( new ContextInspectorListener() {

                        public void predicateSelected(String predicateName, Invocable predicate) {
                            // Empty.
                        }

                        public void predicateDblClicked(String predicateName, Invocable predicate) {
                            setCommand( predicate.toString() );
                        }
                    });
                    ci.show();
                }
            }
        );
        toolsMenu.addItem(showCI);

        menu.addItem(new MenuItem("Console", consoleMenu));
        menu.addItem(new MenuItem("Tools"  , toolsMenu  ));

        // Command area size.
        commandArea.setSize("600px", "60px");
        commandArea.addKeyboardListener(
            new KeyboardListener() {

                public void onKeyDown(Widget widget, char c, int i) {
                    // Empty.
                }

                public void onKeyPress(Widget widget, char c, int i) {
                    switch ( c ) {
                        case KEY_ENTER:
                            processCommand();
                            break;
                        case KEY_UP:
                            System.err.println("KEY UP DETECTED.");
                            // setCommand( getPreviousCommand() );
                            break;
                        case KEY_DOWN:
                            System.err.println("KEY DOWN DETECTED.");
                            // setCommand( getNextCommand() );
                            break;
                    }
                }

                public void onKeyUp(Widget widget, char c, int i) {
                    // Empty.
                }

        });

        // Command button.
        commandButton.setSize("70px","60px");
        commandButton.setText("Execute");
        commandButton.addClickListener( commandButtonListener );

        // Command panel.
        commandPanel.setStylePrimaryName("command-panel");
        commandPanel.setSpacing(5);
        commandPanel.add(commandArea);
        commandPanel.add(commandButton);

        // Split panel.
        splitPanel.setSize("690px", "700px");
        splitPanel.setSplitPosition("620px");
        splitPanel.setTopWidget( new ScrollPanel(commandHistoryPanel) );
        splitPanel.setBottomWidget(commandPanel);

        // Main panel arrangement.
        mainPanel.add(menu);
        mainPanel.add(splitPanel);
        mainPanel.add(statusBar);

        // Main panel border.
        decoratorPanel.setWidget(mainPanel);

        // Add the main panel.
        RootPanel.get("console").add(decoratorPanel);

        // move cursor focus to the text box
        commandArea.setFocus(true);

        // Tries disabling restrictions.
        if( JSFunctions.disableRestrictions() ) {
            setStatusMessage("restrictions: ok" );
        } else {
            setStatusMessage("restrictions: " + JSFunctions.getDisabilitationError() );
        }

    }

    private int historyIndex;

    /**
     * Pushes an history command inside list of commands.
     *
     * @param command
     */
    public void pushHistoryCommand(String command) {
        historyList.add(command);
        historyIndex = historyList.size() -  1;
    }

    /**
     * Returns the previous command going backward until reached end of history list.
     *
     * @return the previous command in history.
     */
    public String getPreviousCommand() {
        if( historyList.isEmpty() ) {
            return getCommand();
        }
        if( historyIndex >= 0 ) {
            return historyList.get( historyIndex-- );
        }
        return historyList.get(historyIndex);
    }

    /**
     * Returns the next command going forward until reached end of history list.
     *
     * @return the next command in history.
     */
    public String getNextCommand() {
        if( historyList.isEmpty() ) {
            return getCommand();
        }
        if( historyIndex < historyList.size() ) {
            return historyList.get( historyIndex++ );
        }
        return historyList.get(historyIndex);
    }

    /**
     * Clears the command history.
     */
    public void clearCommandHistory() {
        historyList.clear();
    }

    /**
     * Panel containing list with command history.
     */
    class CommandHistoryListPanel extends DialogBox {

        /**
         * list box with commands.
         */
        private ListBox commandHistoryLB = new ListBox();

        /**
         * Close panel button.
         */
        private Button closeButton = new Button("Close");

        /**
         * Vertical layout panel.
         */
        private VerticalPanel verticalPanel = new VerticalPanel();

        /**
         * Constructor.
         */
        CommandHistoryListPanel() {

            super(true);

            setAnimationEnabled(true);

            commandHistoryLB.setWidth("260px");
            commandHistoryLB.addChangeListener(
                    new ChangeListener() {
                        public void onChange(Widget widget) {
                            setCommand(  commandHistoryLB.getItemText( commandHistoryLB.getSelectedIndex() ) );                            
                        }
                    });

            ensureDebugId("commandHistoryListPanel");
            setText("Command History");

            closeButton.addClickListener(
                    new ClickListener() {
                        public void onClick(Widget widget) {
                            CommandHistoryListPanel.this.hide();
                        }
                    });

            verticalPanel.setSpacing(4);
            setWidget(verticalPanel);
            verticalPanel.add( commandHistoryLB );
            verticalPanel.setCellHorizontalAlignment(commandHistoryLB, HasHorizontalAlignment.ALIGN_CENTER);
            verticalPanel.add( closeButton );
            verticalPanel.setCellHorizontalAlignment(closeButton     , HasHorizontalAlignment.ALIGN_CENTER);
        }

        void setHistory(List<String> history) {
             commandHistoryLB.clear();
            for( int i = history.size() -1 ; i >= 0; i--) {
                commandHistoryLB.addItem( history.get(i) );
            }
        }

    }

    /**
     * Unique instance of command history panel.
     */
    private final CommandHistoryListPanel commandHistoryListPanel = new CommandHistoryListPanel();

    /**
     * Shows a panel with command history.
     */
    public void showCommandHistory() {
        commandHistoryListPanel.setHistory( historyList );
        commandHistoryListPanel.center();
        commandHistoryListPanel.show();
    }

    /**
     * Sets the status message.
     * 
     * @param msg
     */
    public void setStatusMessage(String msg) {
        statusBar.add(new HTML(msg), DockPanel.EAST);
    }

    /**
     * Returns current command.
     *
     * @return content of command area.
     */
    public String getCommand() {
        return commandArea.getText();
    }

    /**
     * Sets the content of command area.
     */
    public void setCommand(String cmd) {
        commandArea.setText( cmd );
    }

    /**
     * Cleans command area content.
     */
    public void clearCommand() {
        commandArea.setText("");
    }

    /**
     * Removes history content.
     */
    public void clearHistory() {
        commandHistoryPanel.clear();
    }

    /**
     * Processes command adding result to history.
     */
    private void processCommand() {
        commandHistoryPanel.add( executeOperation( getCommand() ) );
        clearCommand();
    }

    /**
     * Executes current command.
     *
     * @param command the command to be executed.
     * @return the widget containing the command result.
     */
    public Widget executeOperation(String command) {

        try {
            Value v = interpreter.process( command );
            return createHistoryEntry( false, command, v );
        } catch (Exception ie) {
            return createHistoryEntry(true, command, new StringValue( createErrorMessage(ie) ) );
        }

    }

    /**
     * Creates an error message with the content of the given exception.
     * 
     * @param ie
     * @return
     */
    private String createErrorMessage(Exception ie) {
        StringBuilder sb = new StringBuilder();
        sb.append("Interpreter exception");
        if( ie.getMessage() != null ) {
            sb.append(": ").append(ie.getMessage());
        } else {
            sb.append(".");
        }
        Throwable cause = ie.getCause();
        while( cause != null  ) {
            sb.append(" Cause: ").append( cause.getMessage() ).append(" ");

            // Prints on system out.
            System.out.println("Cause: ");
            ie.printStackTrace();

            cause = cause.getCause();
        }
        return sb.toString();
    }

    /**
     * Creates an interactive history entry.
     *
     * @param isException <code>true</code> if this entry represents an exception.
     * @param command executed command text.
     * @param commandResult command result as text.
     * @return
     */
    private Widget createHistoryEntry(boolean isException, String command, Value commandResult) {

        // If not exception adds command in history list.
        if( ! isException ) {
            pushHistoryCommand( command );
        }

        return new CommandEntryPanel(this, command,  commandResult, isException, CEP_WIDTH);
    }

    /**
     * Shows a popup window containing the programmative context of console.
     *
     * @param programmative
     */
    private void showProgrammativeContextWindow(String programmative) {
        com.gwtext.client.widgets.Panel windowPanel = new com.gwtext.client.widgets.Panel();
        windowPanel.setHtml(programmative);
        windowPanel.setShadow(true);

        final Window window = new Window();
        window.setTitle("Window Panel");
        window.setIconCls("paste-icon");
        window.setMaximizable(true);
        window.setResizable(true);
        window.setLayout(new FitLayout());
        window.setWidth(400);
        window.setHeight(200);
        window.setModal(false);

        window.add(windowPanel);

        window.show();
    }

}
