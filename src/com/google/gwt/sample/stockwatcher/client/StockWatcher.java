package com.google.gwt.sample.stockwatcher.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcher implements EntryPoint {
	private static final int REFRESH_INTERVAL = 5000; // ms
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	  private HorizontalPanel addPanel = new HorizontalPanel();
	  private TextBox newSymbolTextBox = new TextBox();
	  private Button addStockButton = new Button("Add");
	  private Label lastUpdatedLabel = new Label();
      private ArrayList<String> stocks = new ArrayList<String>();

	  
	  /**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		
//		Create table for stock data
		stocksFlexTable.setText(0,0,"Symbol");
		stocksFlexTable.setText(0,1,"Price");
		stocksFlexTable.setText(0,2,"Change");
		stocksFlexTable.setText(0,3,"Remove");
		
		
//		TODO Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		
		
//		TODO Assemble Main panel.
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);
		
		
//		TODO Associate the Main panel with the HTML host page.
		RootPanel.get("stockList").add(mainPanel);
		
//		TODO Move cursor focus to the input box.	
		newSymbolTextBox.setFocus(true);
		
		Timer refreshTimer = new Timer() {
			
			@Override
			public void run() {
			
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		
		
		
		addStockButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addStock();
			}
		});
		
		
		newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
			}
		});
		
	}
	
	private void refreshWatchList() {

		final double MAX_PRICE = 100.0;
		final double MAX_PRICE_CHANGE = 0.02;
		
		StockPrice[] prices = new StockPrice[stocks.size()];
		for (int i = 0; i < prices.length; i++) {
			double price = Random.nextDouble() * MAX_PRICE;
			double change = price  * MAX_PRICE_CHANGE * (Random.nextDouble() * 2.0 - 1.0);
			
			prices[i] = new StockPrice (stocks.get(i), price, change);
		}
		
		updateTable(prices);

    }
	
	private void updateTable( StockPrice[] prices ) {
		for (int i = 0; i < prices.length; i++) {
			updateTable(prices[i]);
		}
	}
	
	private void updateTable(StockPrice price) {
		if ( ! stocks.contains(price.getSymbol()))
			return;
		
		int row = stocks.indexOf(price.getSymbol()) + 1;
		
		String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(price.getChange());
		String changePercentText = changeFormat.format(price.getChangePercent());
		
		stocksFlexTable.setText(row,1,priceText);
		stocksFlexTable.setText(row,2,changeText + " (" + changePercentText + "%)");
		
	}

	private void addStock() {
		
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		newSymbolTextBox.setFocus(true);
		
		if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
			Window.alert("'" + symbol + "' is not a valid symbol.");
			newSymbolTextBox.selectAll();
			return;
		}
		
		if ( stocks.contains( symbol)) 
			return;
		
		int row = stocksFlexTable.getRowCount();
		stocks.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);
		
		
		
		Button removeStockButton = new Button("x");
		removeStockButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int removedIndex = stocks.indexOf(symbol);
				stocks.remove(removedIndex);
				stocksFlexTable.removeRow(removedIndex + 1);
			}
		});
		
		
		stocksFlexTable.setWidget(row, 3, removeStockButton);
		newSymbolTextBox.setText("");
		
	    // Get the stock price.
		refreshWatchList();
	}
}
