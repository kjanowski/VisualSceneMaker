package de.dfki.vsm.editor;

//~--- non-JDK imports --------------------------------------------------------

import de.dfki.vsm.editor.dialog.FunDefDialog;
import de.dfki.vsm.editor.event.FunctionCreatedEvent;
import de.dfki.vsm.editor.event.FunctionModifiedEvent;
import de.dfki.vsm.editor.event.FunctionSelectedEvent;
import de.dfki.vsm.model.sceneflow.SceneFlow;
import de.dfki.vsm.model.sceneflow.definition.FunDef;
import de.dfki.vsm.model.sceneflow.definition.ParamDef;
import de.dfki.vsm.util.evt.EventCaster;
import de.dfki.vsm.util.evt.EventListener;
import de.dfki.vsm.util.evt.EventObject;
import de.dfki.vsm.util.ios.ResourceLoader;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * @author Sergio Soto
 *
 */
public class FunctionEditor extends JPanel implements EventListener, Observer {
    private final Observable              mObservable  = new Observable();
    private final EventCaster             mEventCaster = EventCaster.getInstance();
    private JSplitPane                    mSplitPane;
    private JScrollPane                   mLeftScrollPanel;
    private JScrollPane                   mRightScrollPanel;
    private JPanel                        mFunctionsPanel;
    private JPanel                        mButtonPanel;
    private JButton                       mRemoveButton;
    private JButton                       mAddFunctionButton;
    private final ArrayList<FunDefDialog> mFunDefDialogList;
    private final SceneFlow               mSceneFlow;

    /**
     *
     */
    public FunctionEditor(SceneFlow sceneflow) {
        mSceneFlow        = sceneflow;
        mFunDefDialogList = new ArrayList<>();
        setLayout(new GridLayout(1, 0));
        initComponents();
        // Add the element editor to the event multicaster
        EventCaster.getInstance().append(this);
    }

    /**
     *
     */
    private void initComponents() {
        mFunctionsPanel = new JPanel();
        mFunctionsPanel.setLayout(new BoxLayout(mFunctionsPanel, BoxLayout.Y_AXIS));
        displayFunctionPanels();
        mLeftScrollPanel = new JScrollPane(mFunctionsPanel);
        mLeftScrollPanel.setMinimumSize(new Dimension(2000, 200));
        mLeftScrollPanel.setBorder(BorderFactory.createEtchedBorder());
        mLeftScrollPanel.getVerticalScrollBar().setValue(0);
        initButtonPanel();
        mRightScrollPanel = new JScrollPane(mButtonPanel);
        mRightScrollPanel.setBorder(BorderFactory.createEtchedBorder());
        mSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        mSplitPane.setBorder(BorderFactory.createEmptyBorder());
        mSplitPane.setLeftComponent(mLeftScrollPanel);
        mSplitPane.setRightComponent(mRightScrollPanel);
        mSplitPane.setResizeWeight(.97d);
        mSplitPane.setBorder(null);
        flattenSplitPane(mSplitPane);
        add(mSplitPane);
    }

    public static void flattenSplitPane(JSplitPane jSplitPane) {
        jSplitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void setBorder(Border b) {}
                };
            }
        });
        jSplitPane.setBorder(null);
    }

    /**
     *
     */
    private void displayFunctionPanels() {

        // Inititalize panel
        mFunDefDialogList.clear();
        mFunctionsPanel.removeAll();

        // Create a FunDefDialog object for every existing function
        // in order to reuse components
        for (final FunDef funDef : mSceneFlow.getUsrCmdDefMap().values()) {
            final FunDefDialog funDefPanel = new FunDefDialog(funDef);

            mFunDefDialogList.add(funDefPanel);

            // Add content of the function container
            JPanel functionContent   = funDefPanel.createPanel();
            JPanel functionContainer = new JPanel();

            functionContainer.setLayout(new BoxLayout(functionContainer, BoxLayout.X_AXIS));
            functionContainer.add(functionContent);

            // add remove button to the far right
            mRemoveButton = new JButton(ResourceLoader.loadImageIcon("/res/img/new/minus.png"));
            mRemoveButton.setMinimumSize(new Dimension(20, 75));
            mRemoveButton.setMaximumSize(new Dimension(20, 75));
            mRemoveButton.setPreferredSize(new Dimension(20, 75));
            mRemoveButton.setOpaque(true);
            mRemoveButton.setBackground(Color.GRAY);
            mRemoveButton.setBorder(BorderFactory.createEmptyBorder());
            mRemoveButton.addActionListener(new ActionListener() {
                @Override
                public synchronized void actionPerformed(ActionEvent e) {
                    removeFunction(funDef);                    
                }
            });
            functionContainer.add(mRemoveButton);
            mFunctionsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
            mFunctionsPanel.add(functionContainer);

            /*
             *   Add action and focus listeners to editable elements from
             *   content to highlight the functionContainer being edited
             *   nd save edited changes
             */
            // Function Name
            funDefPanel.getNameInput().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    funDefPanel.setSelectedBackground(true);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    funDefPanel.setSelectedBackground(false);
                    funDefPanel.getNameInput().setText(funDef.getName());
                    funDefPanel.getNameInput().setForeground(Color.black);
                }
            });
            funDefPanel.getNameInput().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {
                    String newFundDefName = funDefPanel.getNameInput().getText().trim();

                    if (!(funDef.getName().equals(newFundDefName))) {
                        if (!newFundDefName.equals("")) {

                            // look if name is already being used by another command
                            if (mSceneFlow.getUsrCmdDef(newFundDefName) != null) {
                                funDefPanel.getNameInput().setForeground(Color.red);
                            } else {
                                funDefPanel.getNameInput().setForeground(Color.BLACK);
                                mSceneFlow.removeUsrCmdDef(funDef.getName());
                                mSceneFlow.putUsrCmdDef(newFundDefName, funDef);
                                updateFunDef(funDef, funDefPanel);
                            }
                        }
                    }                      
                }
            });            
                       
             // Function Class Name
            
            funDefPanel.getClassNameInput().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {      
                    updateFunDef(funDef, funDefPanel);     
                }
            });
            
            funDefPanel.getClassNameInput().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                   funDefPanel.setSelectedBackground(true);    
                }

                @Override
                public void focusLost(FocusEvent e) {
                    funDefPanel.setSelectedBackground(false); 
                }
            });
            
            // Function Method    
            
            funDefPanel.getMethodBox().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                   funDefPanel.setSelectedBackground(true);    
                }

                @Override
                public void focusLost(FocusEvent e) {
                    funDefPanel.setSelectedBackground(false); 
                }
            });
            
            funDefPanel.getMethodBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) { 
                    
                    if(funDefPanel.getIsValidClass()){
                      
                        
                        if(funDefPanel.getMethodBox().getSelectedItem()!=null){
                            funDefPanel.setSelectedMethod(funDefPanel.getmMethodMap().get((String) funDefPanel.getMethodBox().getSelectedItem()));
                        }                        
                        if (funDefPanel.getSelectedMethod() != null){
                            
                            //updateFunDef(funDef, funDefPanel);
                            
                            String newSelectedMethodName =funDefPanel.getSelectedMethod().getName().trim();
                          
                            funDef.setMethod(newSelectedMethodName);   
                            funDefPanel.getFunDef().setMethod(newSelectedMethodName);                                   
                            funDefPanel.methodComboBoxActionPerformed(evt);
                            funDef.getParamList().clear();
                            Enumeration args = ((DefaultListModel) funDefPanel.getArgList().getModel()).elements();
                            while (args.hasMoreElements()) {
                                String argString = (String) args.nextElement();


                             funDef.addParam(
                                    new ParamDef(funDefPanel.getNameMap().get(argString), funDefPanel.getTypeMap().get(argString)));
                            }
                            
                            Editor.getInstance().update();                             
                        }                        
                    }
                }
            });

            // Function Class Name
            funDefPanel.getClassNameInput().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {
                    updateFunDef(funDef, funDefPanel);
                }
            });

            // Function Method
            funDefPanel.getMethodBox().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    funDefPanel.setSelectedBackground(true);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    funDefPanel.setSelectedBackground(false);
                }
            });
            funDefPanel.getMethodBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (funDefPanel.getIsValidClass()) {
                        if (funDefPanel.getMethodBox().getSelectedItem() != null) {
                            funDefPanel.setSelectedMethod(
                                funDefPanel.getmMethodMap().get((String) funDefPanel.getMethodBox().getSelectedItem()));
                        }

                        if (funDefPanel.getSelectedMethod() != null) {

                            // updateFunDef(funDef, funDefPanel);
                            String newSelectedMethodName = funDefPanel.getSelectedMethod().getName().trim();

                            funDef.setMethod(newSelectedMethodName);
                            funDefPanel.getFunDef().setMethod(newSelectedMethodName);
                            funDefPanel.methodComboBoxActionPerformed(evt);
                            funDef.getParamList().clear();

                            Enumeration args = ((DefaultListModel) funDefPanel.getArgList().getModel()).elements();

                            while (args.hasMoreElements()) {
                                String argString = (String) args.nextElement();

                                funDef.addParam(new ParamDef(funDefPanel.getNameMap().get(argString),
                                                             funDefPanel.getTypeMap().get(argString)));
                            }

                            Editor.getInstance().update();
                        }
                    }
                }
            });

            // Function Arguments
            funDefPanel.getArgList().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    funDefPanel.setSelectedBackground(true);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    funDefPanel.setSelectedBackground(false);
                }
            });
            funDefPanel.getArgList().addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    funDefPanel.argumentListMouseClicked(evt);
                    funDef.getParamList().clear();

                    Enumeration args = ((DefaultListModel) funDefPanel.getArgList().getModel()).elements();

                    while (args.hasMoreElements()) {
                        String argString = (String) args.nextElement();

                        funDef.addParam(new ParamDef(funDefPanel.getNameMap().get(argString),
                                                     funDefPanel.getTypeMap().get(argString)));
                    }

                    Editor.getInstance().update();
                }
            });
        }

        mFunctionsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
    }

    /**
     *
     */
    private void initButtonPanel() {

        // Create Button
        mAddFunctionButton = new JButton();
        updateAddButton();
        mAddFunctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewFunction();
                updateAddButton();
            }
        });

        Action action = new AbstractAction("AddFunction") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewFunction();
            }
        };

        // configure the Action with the accelerator (aka: short cut)
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F"));

        // Button panel
        mButtonPanel = new JPanel(null);
        mButtonPanel.setOpaque(true);
        mButtonPanel.setLayout(new GridLayout(1, 0));
        mButtonPanel.add(mAddFunctionButton);

        // manually register the accelerator in the button's component input map
        mButtonPanel.getActionMap().put("myAction", action);
        mButtonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "myAction");
    }

    /**
     *
     */
    private void addNewFunction() {
        FunDef usrCmdDef = new FunDef("newCommand", "java.lang.System.out", "println");

        usrCmdDef.addParam(new ParamDef("text", "String"));
        updateArguments(usrCmdDef);
        mSceneFlow.putUsrCmdDef(usrCmdDef.getName(), usrCmdDef);
        Editor.getInstance().update();
        EventCaster.getInstance().convey(new FunctionCreatedEvent(this, usrCmdDef));
    }

    /**
     *
     */
    private synchronized void removeFunction(FunDef funDef) {
        if (funDef != null) {
            mSceneFlow.removeUsrCmdDef(funDef.getName());           
            launchFunctionCreatedEvent(funDef);
            Editor.getInstance().update();
        }

       // Editor.getInstance().update();

    }

    public JButton getAddFunctionButton() {
        return mAddFunctionButton;
    }

    /**
     *
     */
    @Override
    public void update(EventObject event) {
        if (event instanceof FunctionSelectedEvent) {
            FunDef functionData = ((FunctionSelectedEvent) event).getFunction();

            for (FunDefDialog currentPanel : mFunDefDialogList) {
                if (functionData.getName().equals(currentPanel.getFunDef().getName())) {
                    currentPanel.setSelectedBackground(true);
                    mLeftScrollPanel.getVerticalScrollBar().setValue(mFunDefDialogList.indexOf(currentPanel) * 75);
                    currentPanel.getNameInput().requestFocusInWindow();
                } else {
                    currentPanel.setSelectedBackground(false);
                }
            }

            updateAddButton();
        } else if (event instanceof FunctionCreatedEvent) {
            displayFunctionPanels();

            // Highlight and set scrollbar to selected function
            FunDef functionData = ((FunctionCreatedEvent) event).getFunction();

            for (FunDefDialog currentPanel : mFunDefDialogList) {
                if (functionData.getName().equals(currentPanel.getFunDef().getName())) {
                    mLeftScrollPanel.getVerticalScrollBar().setValue(mFunDefDialogList.indexOf(currentPanel) * 75);
                } else {
                    currentPanel.setSelectedBackground(false);
                }
            }            
            
            // Look for function in list
            for (FunDefDialog currentPanel : mFunDefDialogList) {
                if (functionData.getName().equals(currentPanel.getFunDef().getName())) {
                    currentPanel.getNameInput().requestFocusInWindow();
                    updateFunDef(functionData, currentPanel);
                } else {
                    currentPanel.setSelectedBackground(false);
                }
            }

            updateAddButton();
        } else if (event instanceof FunctionModifiedEvent) {
            displayFunctionPanels();

            FunDef functionData = ((FunctionModifiedEvent) event).getFunction();

            // Look for function in list
            for (FunDefDialog currentPanel : mFunDefDialogList) {
                if (functionData.getName().equals(currentPanel.getFunDef().getName())) {
                    currentPanel.getNameInput().requestFocusInWindow();
                    updateFunDef(functionData, currentPanel);
                } else {
                    currentPanel.setSelectedBackground(false);
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void update(java.util.Observable obs, Object obj) {
        mObservable.update(obj);
    }

    private void updateFunDef(FunDef funDef, FunDefDialog funDefDialog) {
        funDef.setName(funDefDialog.getNameInput().getText().trim());
        funDef.setClassName(funDefDialog.getClassNameInput().getText().trim());
        funDef.setMethod(funDefDialog.getSelectedMethod().getName().trim());

        // Clear the parameter list and fill it again
        funDef.getParamList().clear();

        Enumeration args = ((DefaultListModel) funDefDialog.getArgList().getModel()).elements();

        while (args.hasMoreElements()) {
            String argString = (String) args.nextElement();

            funDef.addParam(new ParamDef(funDefDialog.getNameMap().get(argString),
                                         funDefDialog.getTypeMap().get(argString)));
        }

        Editor.getInstance().update();
    }

    /**
     *
     */
    private void updateAddButton() {
        mAddFunctionButton.setText("");
        mAddFunctionButton.setIcon(ResourceLoader.loadImageIcon("/res/img/new/plus.png"));
        mAddFunctionButton.setOpaque(true);
        //mAddFunctionButton.setBackground(Color.GRAY);
        mAddFunctionButton.setMaximumSize(new Dimension(15, 15));
    }

    /**
     *
     */
    private void updateArguments(FunDef funDef) {
        final FunDefDialog funDefPanel = new FunDefDialog(funDef);

        funDef.getParamList().clear();

        Enumeration args = ((DefaultListModel) funDefPanel.getArgList().getModel()).elements();

        while (args.hasMoreElements()) {
            String argString = (String) args.nextElement();

            funDef.addParam(new ParamDef(funDefPanel.getNameMap().get(argString),
                                         funDefPanel.getTypeMap().get(argString)));
        }

        Editor.getInstance().update();
    }

    /**
     *
     */
    public ArrayList<FunDefDialog> getFunDefDialogList() {
        return mFunDefDialogList;
    }

    /**
     *
     */
    private void launchFunctionCreatedEvent(FunDef funDef) {
        FunctionCreatedEvent ev = new FunctionCreatedEvent(this, funDef);
        mEventCaster.convey(ev);
    }

    /**
     *
     */
    private class Observable extends java.util.Observable {
        public void update(Object obj) {
            setChanged();
            notifyObservers(obj);
        }
    } 
}
