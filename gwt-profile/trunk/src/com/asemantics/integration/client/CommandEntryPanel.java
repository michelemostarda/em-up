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
import com.asemantics.integration.client.gui.GUIRenderer;
import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.common.Utils.ContentType;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.nativepkg.NativeException;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.layout.FitLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Defines a command entry in <i>MUp Console</i>.
 */
public class CommandEntryPanel extends DecoratorPanel {

    /**
     * Max length of string abstract.
     */
    protected static final int ABSTRACT_MAXLEN = 100;

    /**
     * Editor Width.
     */
    static final int EDITOR_WINDOW_WIDTH  = 400;

    /**
     * Editor height.
     */
    static final int EDITOR_WINDOW_HEIGHT = 400;

    /**
     * Text editor.
     */
    final TextEditorLink TEXT_EDITOR_LINK = new TextEditorLink();

    /**
     * GUI renderer.
     */
    final GUIRendererLink GUI_RENDERER_LINK = new GUIRendererLink();


    /**
     * JSON editor.
     */
    final JSONEditorLink JSON_EDITOR_LINK = new JSONEditorLink();

    /**
     * HTML editor.
     */
    final HTMLRendererLink HTML_RENDERER_LINK = new HTMLRendererLink();

    /**
     * XML editor.
     */
    final XMLEditorLink  XML_EDITOR_LINK  = new XMLEditorLink();

    /**
     * Executed command.
     */
    private String command;

    /**
     * Result of command execution.
     */
    private Value commandResult;

    /**
     * Execution type.
     */
    private boolean isExeption;

    /**
     * Constructor.
     *
     * @param console console generating entry.
     * @param command executed command.
     * @param commandResult returned command result.
     * @param isException if <code>true</code> execution represents an exception.
     * @param width entry width.
     */
    public CommandEntryPanel(
            final MUpConsole console,
            final String command,
            final Value commandResult,
            final boolean isException,
            final int width
    ) {
        if( console == null || command == null || commandResult == null ) {
            throw new NullPointerException();
        }

        this.command       = command;
        this.commandResult = commandResult;
        this.isExeption    = isException;

        // Link to executed command.
        final Hyperlink commandHL = new Hyperlink();
        commandHL.setWidth( width + "px" );
        commandHL.setHeight(Utils.getStringHeight(command, width) + "px" );
        commandHL.setText(command);
        commandHL.addClickListener( new ClickListener() {
            public void onClick(Widget widget) {
                console.setCommand( commandHL.getText() );
            }
        });

        // Text area with command result.
        TextArea resultTA = new TextArea();
        resultTA.setWidth ( width + "px" );
        String commandResultAbstract = getResultAbstract(commandResult);
        resultTA.setHeight(Utils.getStringHeight(commandResultAbstract, width) + "px" );
        resultTA.setEnabled(false);
        resultTA.setText( commandResultAbstract );

        // List of vesualizers applicable to command result.
        VerticalPanel vp = new VerticalPanel();
        vp.add(commandHL);
        vp.add( new ScrollPanel(resultTA) );

        // Expressing contentType.
        HorizontalPanel editorsPanel = new HorizontalPanel();
        editorsPanel.setSpacing(4);
        ContentType contentType = Utils.induceType( commandResult.asString().getNativeValue() );
        Hyperlink[] editors = getApplicableEditors(contentType);
        for(Hyperlink editor : editors) {
            editorsPanel.add( editor );
        }
        vp.add(editorsPanel);

        /**
         * Is exeception custom styling.
         */
        if( isException ) {
            this.setStylePrimaryName("decorator-error");
            resultTA.setStylePrimaryName("decorator-error");
        } else {
            this.setStylePrimaryName("decorator-ok");
            resultTA.setStylePrimaryName("decorator-ok");
        }
        this.setWidget(vp);
    }

    /**
     * Returns a string with abstract of command result expressed as string.
     *
     * @param v operation value.
     * @return string representing abstract of <i>v</i>. 
     */
    protected String getResultAbstract(Value v) {
        String valueStr = v.asString().getNativeValue();
        return valueStr.substring(0, valueStr.length() < ABSTRACT_MAXLEN ? valueStr.length() : ABSTRACT_MAXLEN);
    }

    /**
     * Returns a list of editors applicable on given content type.

     * @param contentType submitted content type.
     * @return list of applicable editors.
     */
    protected Hyperlink[] getApplicableEditors(ContentType contentType) {
        List<Hyperlink> result = new ArrayList<Hyperlink>(5);
        if( ContentType.JSON == contentType ) {
            result.add(JSON_EDITOR_LINK);
            result.add(GUI_RENDERER_LINK);
        }
        if( ContentType.HTML == contentType ) {
            result.add(HTML_RENDERER_LINK);
            result.add(XML_EDITOR_LINK);
        } else if( ContentType.XML == contentType ) {
            result.add(XML_EDITOR_LINK);
        }
        result.add( TEXT_EDITOR_LINK );
        return result.toArray( new Hyperlink[ result.size() ] );
    }

    /**
     * Plain text editor.
     */
    class TextEditorLink extends Hyperlink {

        TextEditorLink() {

            setText("text");

            addClickListener(
                new ClickListener() {
                    public void onClick(Widget widget) {
                        com.gwtext.client.widgets.form.TextArea textArea = new com.gwtext.client.widgets.form.TextArea();
                        textArea.setValue(
                                //Utils.encodeCarrigeReturn(
                                    Utils.encodeStringAsHTML(commandResult.asString().getNativeValue(), true )
                                //)
                        );
                        textArea.setWidth(EDITOR_WINDOW_WIDTH);
                        textArea.setHeight(EDITOR_WINDOW_HEIGHT);
                        Window window = new Window();
                        window.setTitle("Text Editor");
                        window.setIconCls("paste-icon");
                        window.setMaximizable(true);
                        window.setResizable(true);
                        window.setLayout(new FitLayout());
                        window.setWidth(EDITOR_WINDOW_WIDTH);
                        window.setHeight(EDITOR_WINDOW_HEIGHT);
                        window.setModal(false);

                        window.add(textArea);
                        window.show();
                    }
                }
            );
        }
    }

    /**
     * JSON editor.
     */
    class JSONEditorLink extends Hyperlink {

        JSONEditorLink() {

            setText("json");

            addClickListener(
                new ClickListener() {
                    public void onClick(Widget widget) {
                        ObjectInspector objectInspector = new ObjectInspector(
                                commandResult.asJsonValue().getJsonBase()
                        );
                        objectInspector.show();
                    }
                }
            );
        }
    }

    /**
     * GUI renderer.
     */
    class GUIRendererLink extends Hyperlink {

        GUIRendererLink() {

            setText("renderer");

            addClickListener(
                new ClickListener() {
                    public void onClick(Widget widget) {
                        JsonBase jsonBase = null;
                        try {
                            jsonBase = NativeImpl.getInstance().parseJSON( commandResult.asString().getNativeValue() );
                        } catch (NativeException ne) {
                            MessageBox.alert("Parse error:", "Cannot parse command result as valid JSON: " + ne.getMessage());
                        }
                        GUIRenderer guiRenderer = new GUIRenderer(jsonBase);
                        guiRenderer.show();
                    }
                }
            );
        }
    }

    /**
     * HTML redenderer.
     */
    class HTMLRendererLink extends Hyperlink {

        HTMLRendererLink() {

            setText("html");

            addClickListener(
                new ClickListener() {
                    public void onClick(Widget widget) {
                        HTMLPanel htmlPanel = new HTMLPanel( commandResult.asString().getNativeValue() );
                        Window window = new Window();
                        window.add(htmlPanel);
                        window.setLayout(new FitLayout());
                        window.setWidth(EDITOR_WINDOW_WIDTH);
                        window.setHeight(EDITOR_WINDOW_HEIGHT);
                        window.setModal(false);
                        window.setClosable(true);
                        window.show();
                    }
                }
            );
        }
    }

    /**
     * XML editor.
     *
     * TODO: Complete this with the Ext widget.
     */
    class XMLEditorLink extends Hyperlink {

        XMLEditorLink() {

            setText("xml");

            addClickListener(
                new ClickListener() {
                    public void onClick(Widget widget) {
                        com.gwtext.client.widgets.Panel panel = new com.gwtext.client.widgets.Panel();
                        panel.setShadow(true);
                        panel.setHtml(
                            Utils.encodeStringAsHTML( commandResult.asString().getNativeValue() )
                        );

                        Window window = new Window();
                        window.setTitle("Window Panel");
                        window.setIconCls("paste-icon");
                        window.setMaximizable(true);
                        window.setResizable(true);
                        window.setLayout(new FitLayout());
                        window.setWidth(EDITOR_WINDOW_WIDTH);
                        window.setHeight(EDITOR_WINDOW_HEIGHT);
                        window.setModal(false);

                        window.add(panel);
                        window.show();
                    }
                }
            );
        }
    }

}

