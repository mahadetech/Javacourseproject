package xmemory;
        
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.net.URL;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/** 
  * XMemory<br>
  */
class XMemory extends JPanel {

/*
   * Constants
   */
  private static final long serialVersionUID = -4834428829531522611L;

  // String constants
 
  private final static String sTitle            = "XMemory";
  private final static String sAbout = sTitle + ". spielen normale memory spiele";
  private final static String sNewGame          = "Neues Spiel";
  private final static String sCrossPF          = "Cross Platform";
  private final static String sSystem           = "System";
  private final static String sUILF             = "Look & Feel";
  private final static String sToolTips         = "Tool Tips";
  private final static String sRows             = "Zeilen";
  private final static String sCols             = "Spalten";
  private final static String sTries            = "Versuche";
  private final static String sWrongTries       = "Fehler";
  private final static String sImageSet         = "Bilder Schema";
  private final static String sMenuGame         = "Spiel";
  private final static String sMenuOptions      = "Einstellungen";
  private final static String sMenuHelp         = "Hilfe";
  private final static String sItemGameNew      = "Neu";
  private final static String sItemGameExit     = "Beenden";
  private final static String sItemHelpAbout    = "Info";

  private final static String sTipItemGameNew         = "Neues Spiel beginnen";
  private final static String sTipItemGameExit        = "Spiel beenden";
  private final static String sTipMenuOptionColsRows  = "Anzahl Spalten/Zeilen festlegen";
  private final static String sTipMenuOptionUILF      = "Generelles Aussehen";
  private final static String sTipMenuOptionTT        = "Ein/Aus dieser Tips";
  private final static String sNotEnoughImages        = "Zu wenig Bilder vorhanden f�r Spalten*Zeilen";

  private final static String sAcceleratorHelpAbout  = "F1";

  private final static int iMnemonicGame       = KeyEvent.VK_S;
  private final static int iMnemonicOptions    = KeyEvent.VK_E;
  private final static int iMnemonicHelp       = KeyEvent.VK_H;

  private final static int iMnemonicGameNew    = KeyEvent.VK_N;
  private final static int iMnemonicGameExit   = KeyEvent.VK_B;
  private final static int iMnemonicOptionsCR  = KeyEvent.VK_Z;
  private final static int iMnemonicOptionsLF  = KeyEvent.VK_L;
  private final static int iMnemonicOptionsTT  = KeyEvent.VK_T;
  private final static int iMnemonicHelpAbout  = KeyEvent.VK_I;

  // Initial application size
  private final static int startSizeX = 800;
  private final static int startSizeY = 600;

  // Fontsizes
  private final static int triesFontSize = 18;
  private final static int imageSetFontSize = 10;
  private final static int newGameButtonFontSize = 18;

  // Number of Cards
  private final static int maxCardCols = 10;
  private final static int maxCardRows = 8;
  private final static int maxNumCards = maxCardCols*maxCardRows;

  //private final static int maxNumImages = 40;     // This must match with the imageSets used

  /*
   * Variables
   */
  // New Game Button
  private JButton newGameButton = new JButton(sNewGame);

  // Cards
  private Card[] cards = new Card[maxNumCards];
  private Card[] openCards = new Card[2];
  private int openCardNum = 0;

  // XMLReader
  private static String sImageInfoFile = "XMInfo.xml";
  private static XMLReader xmlReader = null;

  // Images
  private Image[] images = null;
  private String[] imageTitle = null;
  private String[] imageCardText = null;
  private boolean[] imageUsed = null;
  // Image Sets
  private JRadioButton[] imageSet = null;
  private int imageSetNum = 0;
  private int imageSetUsed = 0;
  private int imageSetNumImages[] = null;
  private org.w3c.dom.Element imageSetSchemaElement[] = null;

  // Options
  private JScrollBar cardColsScroller;
  private JScrollBar cardRowsScroller;
  private JLabel cardColsValueLabel;
  private JLabel cardRowsValueLabel;
  private int cardCols = Math.min( maxCardCols, Math.max( 6, maxCardCols/2 ) );
  private int cardRows = Math.min( maxCardRows, Math.max( 4, maxCardRows/2 ) );

  // Number Tries
  private JLabel numTriesLabel;
  private JLabel numWrongTriesLabel;
  private int numTries = 0;
  private int numWrongTries = 0;

  // Number of cards use in current game
  private int numCards = cardRows*cardCols;

  // Initial Card Text
  private String cardText = "XMemory";

  // Cards Grid
  JPanel cardGrid = new JPanel();

  // Game Panel
  JPanel gamePanel = new JPanel();

  /**
    *  XMemory constructor
    *  sets main application layout
    */
  XMemory() {
    super(true);

    // Set up System Look&Feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch ( Exception ex ) {
      JOptionPane.showMessageDialog( null, ex.toString() );
    }

    // Get Font
    Font font = getFont();

    // XMLReader
    xmlReader = new XMLReader();

    // Setup Options Panel
    JPanel northPanel = new JPanel();

    JPanel statusPanel = new JPanel();

    // numTries / numWrongTries
    JLabel triesLabel = new JLabel(sTries);
    JLabel wrongTriesLabel = new JLabel(sWrongTries);
    numTriesLabel = new JLabel(" "+numTries);
    numWrongTriesLabel = new JLabel(" "+numWrongTries);

    // tries Font
    if ( font != null ) {
      font = new Font( font.getName(), Font.BOLD, triesFontSize );
    }
    if ( font != null ) {
      triesLabel.setFont( font );
      wrongTriesLabel.setFont( font );
      numTriesLabel.setFont( font );
      numWrongTriesLabel.setFont( font );
    }

    statusPanel.setLayout( new GridLayout(1,2));
    statusPanel.add(triesLabel);
    statusPanel.add(numTriesLabel);
    statusPanel.add(wrongTriesLabel);
    statusPanel.add(numWrongTriesLabel);

    northPanel.add(statusPanel);

    // Setup Options Panel
    int isn;
    JPanel southPanel = new JPanel();
    southPanel.setLayout( new GridLayout(2,1) );

    JPanel imageSetPanel = new JPanel();
    JLabel imageSetLabel = new JLabel(sImageSet);
    ButtonGroup imageSetGroup = new ButtonGroup();
    for ( isn = 0; isn < imageSetNum; isn++ ) {
      imageSetGroup.add(imageSet[isn]);
    }

    if ( font != null ) {
      font = new Font( font.getName(), Font.BOLD, imageSetFontSize );
    }
    if ( font != null ) {
      imageSetLabel.setFont( font );
      for ( isn = 0; isn < imageSetNum; isn++ ) {
        imageSet[isn].setFont(font);
      }
    }
    imageSetPanel.add(imageSetLabel);
    for ( isn = 0; isn < imageSetNum; isn++ ) {
      imageSetPanel.add(imageSet[isn]);
    }

    // New Game Button
    if ( font != null ) {
      font = new Font( font.getName(), Font.BOLD, newGameButtonFontSize );
    }
    if ( font != null ) {
      newGameButton.setFont( font );
    }
    if ( (cardRows*cardCols)%2 == 0 ) {
      newGameButton.setEnabled(true);
    } else {
      newGameButton.setEnabled(false);
    }

    newGameButton.addActionListener( new NewGameListener() );

    southPanel.add(imageSetPanel);
    southPanel.add(newGameButton);

    // Game Panel
    gamePanel = new JPanel();
    gamePanel.setLayout(new BorderLayout());
    gamePanel.add("North",northPanel);
    gamePanel.add("Center",cardGrid);
    gamePanel.add("South",southPanel);

    // Main Layout
    setLayout(new BorderLayout());
    add("North", createMenubar() );
    add("Center", gamePanel );

    // ToolTipManager
    ToolTipManager.sharedInstance().setInitialDelay(100);
  }

  /*
   * Main
   */
  public static void main(String args[]) {

    try {
      checkArgs(args);

      JFrame frame = new JFrame();

      XMemory memory = new XMemory();

      Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
      frame.setCursor( handCursor );

      frame.setTitle(sTitle);
      frame.getContentPane().add( memory );
      frame.addWindowListener(new MainWindowAdapter());
      frame.setSize(startSizeX, startSizeY);
      frame.setVisible(true);
    } catch ( Exception ex ) {
      JOptionPane.showMessageDialog( null, ex.toString() );
    }
  }

  /**
    *  checkArgs()
    *  - check args
    *  - call usage() ( which exits ) if parameters a not correct
    */
  private static void checkArgs( String[] args )
  {
    int i;
    boolean callUsage = false;
    String  usageText = "";

    // iterate argument list
    for ( i = 0; i < args.length; i++ )
    {
      String opt = args[i].substring( 0, 1 );
      if ( args[i].substring( 0, 1 ).equals( "/" ) ||
           args[i].substring( 0, 1 ).equals( "-" ) )
      {
        // Options
        if ( args[i].length() == 5 && args[i].substring( 1 ).equals( "help" ) ) {
          callUsage = true;
          usageText = "";
        } else if ( args[i].length() > 5 && args[i].substring( 1, 5 ).equals( "xml=" ) ) {
          sImageInfoFile = args[i].substring(5);
        } else {
          // Unknow Option
          callUsage = true;
          usageText = "Unknown Option: "+args[i];
        }
      }
      else
      {
        // non-option parameter
        callUsage = true;
        usageText = "Unknown Parameter: "+args[i];
      }
    }

    if (callUsage) {
      usage( usageText );
    }
  }

  /**
    *  usage
    */
  private static void usage( String errorDescription )
  {
    System.out.println( "Usage: java "+sTitle+" [Options]" );
    System.out.println( " /xml=xmlinfo-file (default=XMInfo.xml)" );
    System.out.println( " /help" );

    // Error Description
    if ( errorDescription.length() > 0 )
    {
      System.out.println( "" );
      System.out.println( errorDescription );
    }

    // Exit
    System.exit( 0 );
  }

  /**
    *  applications main WindowAdapter
    */
  private static final class MainWindowAdapter extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      Close();
    }
    public void windowActivated(WindowEvent e) {
      e.getWindow().repaint();
    }
  }

  /**
    *  applications main close function
    */
  private static void Close() {
    System.exit(0);
  }

  /**
   * createMenubar()
   *  create the menubar
   *  add Mnemonics
   *  add ToolTips
   *  add ActionListeners
   *  setup ComponentEnablers
   */
  private JMenuBar createMenubar()
  {
    JMenuBar mb = new JMenuBar();

    /*
     * Menu Game
     */
    JMenu menuGame         = new JMenu(sMenuGame);
    JMenuItem itemGameNew  = new JMenuItem(sItemGameNew, iMnemonicGameNew);
    JMenuItem itemGameExit = new JMenuItem(sItemGameExit, iMnemonicGameExit);

    menuGame.setMnemonic(iMnemonicGame);

    itemGameNew.setToolTipText(sTipItemGameNew);
    itemGameExit.setToolTipText(sTipItemGameExit);

    itemGameNew.addActionListener(new NewGameListener());
    itemGameExit.addActionListener(new ExitGameListener());

    menuGame.add( itemGameNew );
    menuGame.addSeparator();
    menuGame.add( itemGameExit );

    /*
     * Menu Options
     */
    JMenu menuOptions        = new JMenu(sMenuOptions);
    JMenu menuOptionColsRows = new JMenu(sCols+"/"+sRows);
    JMenu menuOptionUILF     = new JMenu(sUILF);

    // ToolTips Option (On/Off)
    JCheckBoxMenuItem menuOptionToolTips = 
      new JCheckBoxMenuItem(sToolTips, true);
    menuOptionToolTips.addChangeListener( new ToolTipsListener() );
    menuOptionToolTips.setMnemonic(iMnemonicOptionsTT);
    menuOptionToolTips.setToolTipText(sTipMenuOptionTT);

    // Option Rows/Cols
    JPanel colsRowsPanel = new JPanel();
    JPanel colsPanel = new JPanel();
    JPanel rowsPanel = new JPanel();
    colsRowsPanel.setLayout( new GridLayout(2,1,5,5) );
    colsPanel.setLayout( new BorderLayout() );
    rowsPanel.setLayout( new BorderLayout() );

    // Rows
    cardRowsScroller = new JScrollBar( JScrollBar.VERTICAL, 1, 1, 1, maxCardRows + 1 );
    cardRowsScroller.setValue(cardRows);
    cardRowsScroller.addAdjustmentListener(new CardRowsListener() );
    cardRowsValueLabel = new JLabel( ""+cardRows+" " );
    cardRowsValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    rowsPanel.add("West",cardRowsScroller);
    rowsPanel.add("East",cardRowsValueLabel);

    // Cols
    cardColsScroller = new JScrollBar( JScrollBar.HORIZONTAL, 1, 1, 1, maxCardCols + 1 );
    cardColsScroller.setValue(cardCols);
    cardColsScroller.addAdjustmentListener(new CardColsListener() );
    cardColsValueLabel = new JLabel( ""+cardCols );
    cardColsValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    colsPanel.add("North",cardColsScroller);
    colsPanel.add("South",cardColsValueLabel);

    colsRowsPanel.add(colsPanel);
    colsRowsPanel.add(rowsPanel);

    menuOptionColsRows.setToolTipText(sTipMenuOptionColsRows);
    menuOptionColsRows.setMnemonic(iMnemonicOptionsCR);
    menuOptionColsRows.add(colsRowsPanel);

    // Option UI L&F
    ButtonGroup uiLFGroup = new ButtonGroup();
    JRadioButtonMenuItem uiLFSystem  = new JRadioButtonMenuItem(sSystem,true);
    JRadioButtonMenuItem uiLFCrossPF = new JRadioButtonMenuItem(sCrossPF,false);

    uiLFSystem.addActionListener(new UILFSystemListener());
    uiLFCrossPF.addActionListener(new UILFCrossPFListener());

    uiLFGroup.add(uiLFSystem);
    uiLFGroup.add(uiLFCrossPF);

    menuOptionUILF.setToolTipText(sTipMenuOptionUILF);
    menuOptionUILF.setMnemonic(iMnemonicOptionsLF);
    menuOptionUILF.add(uiLFSystem);
    menuOptionUILF.add(uiLFCrossPF);

    menuOptions.setMnemonic(iMnemonicOptions);

    menuOptions.add( menuOptionColsRows );
    menuOptions.add( menuOptionUILF );
    menuOptions.add( menuOptionToolTips );

    /*
     * Menu Help
     */
    JMenu menuHelp          = new JMenu(sMenuHelp);
    JMenuItem itemHelpAbout = new JMenuItem(sItemHelpAbout, iMnemonicHelpAbout);

    menuHelp.setMnemonic(iMnemonicHelp);

    itemHelpAbout.setAccelerator(KeyStroke.getKeyStroke(sAcceleratorHelpAbout));

    itemHelpAbout.addActionListener(new HelpAboutListener());

    menuHelp.add( itemHelpAbout );

    /*
     * MenuBar
     */
    mb.add( menuGame );
    mb.add( menuOptions );
    mb.add( menuHelp );

    return mb;
  }

  /**
    * AdjustmentListener for cardRows
    */
  private class CardRowsListener implements AdjustmentListener {

    public void adjustmentValueChanged(AdjustmentEvent evt) {
      JScrollBar source = (JScrollBar)evt.getSource();
      cardRows = source.getValue();
      cardRowsValueLabel.setText( ""+cardRows+" " );
      if ( (cardRows*cardCols)%2 == 0 &&
           cardRows*cardCols <= 2*imageSetNumImages[imageSetUsed] ) {
        newGameButton.setEnabled(true);
      } else {
        newGameButton.setEnabled(false);
      }
    }
  }

  /**
    * AdjustmentListener for cardCols
    */
  private class CardColsListener implements AdjustmentListener {

    public void adjustmentValueChanged(AdjustmentEvent evt) {
      JScrollBar source = (JScrollBar)evt.getSource();
      cardCols = source.getValue();
      cardColsValueLabel.setText( ""+cardCols );
      if ( (cardRows*cardCols)%2 == 0 &&
           cardRows*cardCols <= 2*imageSetNumImages[imageSetUsed] ) {
        newGameButton.setEnabled(true);
      } else {
        newGameButton.setEnabled(false);
      }
    }
  }

  /**
    * ActionListener for cards Buttons
    */
  private class CardsListener implements ActionListener {

    public void actionPerformed(ActionEvent evt) {
      Card card = (Card)evt.getSource();
      int i=0;
      while ( !cards[i].equals(card) ) {
        i++;
        if ( i>=numCards ) {  
          // This would be a programming error
          // JOptionPane.showMessageDialog( null, "Error: card not found" );
          return;
        }
      }

      openCardNum++;

      switch ( openCardNum ) {
      case 1:                                     // First card opened
        card.showFrontside();                     // Show first card
        numTries++;                               // increase numTries
        numTriesLabel.setText(" "+numTries);      // show numTries
        openCards[0] = card;
        openCards[0].setToolTipText(imageTitle[openCards[0].imageNum]);
        break;
      case 2:                                     // Second card opened
        if ( card == openCards[0] ) {             // Same as first
          card.showBackside();                    // Flip back
          if (card.tries > 1) {
            numWrongTries++;
            numWrongTriesLabel.
              setText(" "+numWrongTries);         // show numWrongTries
          }
          openCardNum = 0;                        // Reset openCardNum
          openCards[0].setToolTipText(null);
        } else {
          card.showFrontside();                   // Show second card
          openCards[1] = card;
          openCards[1].setToolTipText(imageTitle[openCards[1].imageNum]);

          if ( openCards[0].imageNum == openCards[1].imageNum ) {
            // Cards match, make them brighter
            openCards[0].bg = openCards[0].bg.brighter();
            openCards[1].bg = openCards[1].bg.brighter();
            openCards[0].setEnabled(false);
            openCards[1].setEnabled(false);
            // Reset openCardNum
            openCardNum = 0;
          } else {
            if (openCards[0].tries > 1 || openCards[1].tries > 1) {
              numWrongTries++;
              numWrongTriesLabel.
                setText(" "+numWrongTries);       // show numWrongTries
            }
          }
        }
        break;
      default:                                    // 'Third' -> First Card.
        openCards[0].showBackside();              // Hide first ..
        openCards[1].showBackside();              // and second card
        openCards[0].setToolTipText(null);
        openCards[1].setToolTipText(null);

        openCardNum = 1;                          // actually it the first opened card

        card.showFrontside();                     // show 'first' card
        numTries++;                               // increase numTries
        numTriesLabel.setText(" "+numTries);      // show numTries
        openCards[0] = card;
        openCards[0].setToolTipText(imageTitle[openCards[0].imageNum]);
        break;
      }
    }
  }

  /**
    * ActionListener for Game-New
    */
  private class NewGameListener implements ActionListener {

    public void actionPerformed(ActionEvent evt) {
      numCards = cardRows*cardCols;

      if (numCards%2 == 1 ||
          numCards > 2*imageSetNumImages[imageSetUsed]) {
        // Incorrect number of cards, return
        return;
      }

      cardGrid.setVisible(false);
      gamePanel.remove(cardGrid);
      getImageSet();
      buildGrid();
      gamePanel.add("Center",cardGrid);
      openCardNum = 0;
      numTries = 0;
      numTriesLabel.
        setText(" "+numTries);                    // show numWrongTries
      numWrongTries = 0;
      numWrongTriesLabel.
        setText(" "+numWrongTries);               // show numWrongTries
      cardGrid.setVisible(true);
    }
  }

  /**
    *  ActionListener for Game-Exit
    */
  private class ExitGameListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      Close();
    }
  }

  /**
    *  ActionListener for Help-About
    */
  private class HelpAboutListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
     // JOptionPane.showMessageDialog( null, sAbout+". JRE:"+System.getProperty("java.version")+" "+System.getProperty("sun.arch.data.model")+"bit" );
      JOptionPane.showMessageDialog( null, sAbout );
 
    }
  }

  /**
    *  ActionListener for UI L&F System
    */
  private class UILFSystemListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      // Set up System Look&Feel
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch ( Exception ex ) {
        JOptionPane.showMessageDialog( null, ex.toString() );
      }
    }
  }

  /**
    *  ActionListener for UI L&F Cross Platform
    */
  private class UILFCrossPFListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      // Set up System Look&Feel
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch ( Exception ex ) {
        JOptionPane.showMessageDialog( null, ex.toString() );
      }
    }
  }                       

  /**
    * ChangeListener for toolTips
    */
  private class ToolTipsListener implements ChangeListener {

    public void stateChanged(ChangeEvent evt) {
      JCheckBoxMenuItem source = (JCheckBoxMenuItem)evt.getSource();
      if (source.getState()) {
        // Enable ToolTipManager
        ToolTipManager.sharedInstance().setEnabled(true);
      } else {
        // Enable ToolTipManager
        ToolTipManager.sharedInstance().setEnabled(false);
      }

    }
  }

  /**
   * loads image Set
   */
  private void getImageSet() {
    // Determine imageSet
    for (imageSetUsed=0; imageSetUsed<imageSetNum; imageSetUsed++) {
      if (imageSet[imageSetUsed].isSelected()) {
        break;
      }
    }

    // Allocate Images
    images = new Image[imageSetNumImages[imageSetUsed]];
    imageTitle = new String[imageSetNumImages[imageSetUsed]];
    imageCardText = new String[imageSetNumImages[imageSetUsed]];
    imageUsed = new boolean[imageSetNumImages[imageSetUsed]];

    // Get images
    xmlReader.traverseImageForSchema(imageSetSchemaElement[imageSetUsed]);
  }

  /**
   * Helper function for buildGrid
   */
  private void buildGrid() {

    int c;
    Random qqq = new Random();

    // (Re-)Initialize cardGrid
    cardGrid.removeAll();
    cardGrid.setLayout( new GridLayout(cardRows,cardCols) );

    // Initialize all cards and cardImages
    for ( c = 0; c < numCards; c++ ) {
      cards[c] = new Card( cardText );
      cards[c].addActionListener( new CardsListener() );
      cardGrid.add(cards[c]);
    }

    // Initialize imageUsed flag
    for ( c = 0; c < imageSetNumImages[imageSetUsed]; c++ ) {
      imageUsed[c] = false;
    }

    // Randomly assign cardImages
    for ( int cardCnt2 = 1; cardCnt2 <= numCards/2; cardCnt2++ ) {

      // Select random image
      int rand1 = Math.abs(qqq.nextInt()) % imageSetNumImages[imageSetUsed];

      // Select first 'real' free image
      while ( imageUsed[rand1] ) {
        rand1++;
        rand1 %= imageSetNumImages[imageSetUsed];
      }
      // Mark image as used
      imageUsed[rand1] = true;

      // Always set a pair of cards with the same value
      for ( int pair = 0; pair < 2; pair++ ) {

        // Select random card
        int rand2 = Math.abs(qqq.nextInt()) % numCards;

        // Select first free card
        while ( cards[rand2].imageNum!=-1 ) {
          rand2++;
          rand2 %= numCards;
        }

        // Assign values
        cards[rand2].imageNum = rand1;
        cards[rand2].image = images[rand1];
        cards[rand2].setText( imageCardText[rand1] );

        cards[rand2].setPressedIcon(new ImageIcon(images[rand1]));
      }
    }
  }

  /**
   * Card class extends JButton
   * defined for Card specific Button features
   * defines own paint()
   */
  private class Card extends JButton {
	private static final long serialVersionUID = 7988302856295686579L;
	private boolean drawImage = false;
    private int     imageNum  = -1;
    private int     tries     = 0;
    private Image   image     = null;
    private Color   bg;

    // Constructor
    Card(String text) {
      super(text);
      bg = getBackground();
    }

    // Show backside of card ( Card.paint() draws no image)
    void showBackside() {
      drawImage = false;
      repaint();
    }

    // Show card with image ( Card.paint() draws image )
    void showFrontside() {
      drawImage = true;
      tries++;
      repaint();
    }

    // paints the image if drawImage is true
    public void paint( Graphics g ) {
      if ( drawImage ) {
        // Get some info
        Dimension size = getSize();
        int maxWidth = size.width;
        int maxHeight = size.height;
        // Clear first ...
        g.setColor(bg);
        g.fillRect(0,0,maxWidth,maxHeight);            
        // Now get image info
        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);

        if (imageWidth > 0 && imageHeight > 0) {
          // Determine borders
          int dw = 2;
          int dh = 2;
          while ( dw>0 && maxWidth<=2*dw ) {
            dw--;
          }
          while ( dh>0 && maxHeight<=2*dh ) {
            dh--;
          }
          // Adjust size
          maxWidth -= 2*dw;
          maxHeight -= 2*dh;
          // Determine new image sizes
          if ( maxWidth*imageHeight >= maxHeight*imageWidth ) {
            imageWidth = ( maxHeight * imageWidth ) / imageHeight;
            imageHeight = maxHeight;
          } else {
            imageHeight = ( maxWidth * imageHeight ) / imageWidth;
            imageWidth = maxWidth;
          }
          // Draw the image
          g.drawImage( image, (size.width-imageWidth)/2, (size.height-imageHeight)/2, imageWidth, imageHeight, this );
        }
      } else {
        super.paint(g);
      }
    }
  }

  /**
   * XMLReader class
   * parse XMInfo.xml, get DOM, extract info 
   */
  private class XMLReader implements ErrorHandler {
  
    private org.w3c.dom.Document dom;
    private org.w3c.dom.NodeList schemas;
        
    // XMLReader()
    XMLReader() {
      try {
        DocumentBuilderFactory domBuilderFactory = 
          DocumentBuilderFactory.newInstance();
  
        domBuilderFactory.setValidating(true);
  
        DocumentBuilder domBuilder = 
          domBuilderFactory.newDocumentBuilder();
  
        domBuilder.setErrorHandler(this);
  
        URL url = getClass().getResource(sImageInfoFile);

        if (url == null) {
          System.err.println("Fehler: Ung�ltige XML Info Datei "+sImageInfoFile);
          System.exit(0);
        }

        dom = domBuilder.parse( url.toString() );

        schemas = getSchemas(dom);
  
      } catch ( FactoryConfigurationError fcErr ) {
        System.out.println( fcErr.toString() );
        System.exit(0);
      } catch ( ParserConfigurationException pcEx ) {
        System.out.println( pcEx.toString() );
        System.exit(0);
      } catch ( SAXException saxEx ) {
        System.out.println( saxEx.toString() );
        System.exit(0);
      } catch ( IOException ioEx ) {
        System.out.println( ioEx.toString() );
        System.exit(0);
      } catch ( Exception ex ) {
        System.out.println( ex.toString() );
      }
    }
  
    //
    // ErrorHandler methods
    //
  
    // Warning
    public void warning(SAXParseException ex) {
        System.err.println("[Warnung] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }
  
    // Error
    public void error(SAXParseException ex) {
        System.err.println("[Fehler] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
        System.exit(0);
    }
  
    // Fatal error
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Schwerer Fehler] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
        throw ex;
    }
  
    //
    // Helper functions
    //

    // Returns a string of the location
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();
  
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) 
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());
  
        return str.toString();
  
    }
  
    //
    // DOM Traverse functions
    //

    // get NodeList for Elements 'schema'
    public NodeList getSchemas(Node node) {
  
      // is there anything to do?
      if (node == null) {
        return null;
      }

      NodeList schemas = 
        ((org.w3c.dom.Document)node).getElementsByTagName("schema");

      if (schemas != null) {
        int len = schemas.getLength();

        // Allocate 'imageSet's
        imageSet = new JRadioButton[len];
        imageSetNum = len;
        imageSetNumImages = new int[len];
        imageSetSchemaElement = new org.w3c.dom.Element[len];

        for (int i = 0; i < len; i++) {
          imageSetNumImages[i]=0;
          traverseSchema( (org.w3c.dom.Element)schemas.item(i), i );
        }
      }

      return schemas;
    }

    // Traverses Elemets 'schema'
    public void traverseSchema(org.w3c.dom.Element schema, int iID ) {

      String name;

      // is there anything to do?
      if (schema == null || 
          (name = schema.getTagName()) != "schema" ) {
        return;
      }

      imageSetSchemaElement[iID] = schema;

      String id = schema.getAttribute("id");
      if (id != null) {
        imageSet[iID] = new JRadioButton( id, (iID==0) );
      }

      NodeList images = schema.getElementsByTagName("image");

      if (images != null) {
        int len = images.getLength();
        imageSetNumImages[iID] = len;
      }

      if (numCards > imageSetNumImages[iID]*2) {
        System.err.println("Schema "+id+" hat nicht gen�gend Bilder ("+imageSetNumImages[iID]+")");
        System.err.println("(Minimum = "+(int)((numCards+1)/2)+")");
        System.err.println("Bitte Datei "+sImageInfoFile+" korrigieren.");
        System.exit(0);
      }
    }
  
    // Traverses Elemets 'image' for Schema
    public void traverseImageForSchema(org.w3c.dom.Element schema) {

      String name;

      // is there anything to do?
      if (schema == null || 
          (name = schema.getTagName()) != "schema" ) {
        return;
      }

      cardText = schema.getAttribute("id");

      NodeList images = schema.getElementsByTagName("image");

      if (images != null) {
        int len = images.getLength();
        for (int i = 0; i < len; i++) {
          traverseImage((org.w3c.dom.Element)images.item(i), i);
        }
      }
    }
  
    // Traverses Elements 'image'
    public void traverseImage(org.w3c.dom.Element image, int icnt) {

      String name;

      // is there anything to do?
      if (image == null || 
          (name = image.getTagName()) != "image" ) {
        return;
      }

      String file = image.getAttribute("file");
      String title = image.getAttribute("title");

      URL url = getClass().getResource(file);

      if ( url != null ) {
        images[icnt] = Toolkit.getDefaultToolkit().getImage( url );
        imageCardText[icnt] = cardText;
      } else {
        images[icnt] = null;
      }

      if (images[icnt] == null) {
        images[icnt] = new BufferedImage( 128, 128, BufferedImage.TYPE_INT_RGB);
        imageCardText[icnt] = file+" = null";
      }

      imageTitle[icnt] = title;
      imageUsed[icnt] = false;
    }
  }
}
