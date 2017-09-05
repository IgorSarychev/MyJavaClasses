package jforex;

import com.dukascopy.api.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.TimeZone;

@RequiresFullAccess
public class News_Orders implements IStrategy {

        private IContext context = null;
        private IEngine engine = null;      
        private IHistory history = null;
        private IConsole console = null;
        private IIndicators indicators = null;
                         
        public int tagCounter = 1; // переменная для лейбла                                     
        public double pip = 0; // аналог Point
        public int digits = 0; // аналог Digits
                
        @Configurable("Instrument") // инструмент торговли
        public Instrument currentInstrument = Instrument.EURUSD;
        
        @Configurable("Trade BUY")   //
        public boolean buy = true;    //  выбираем торговлю на бай или селл
        @Configurable("Trade SELL") //
        public boolean sell = false;  //               
        
        @Configurable("")
        public int Amount_Orders = 1;          
                                
        @Configurable("")
        public double Size_Lot = 0.1;

        @Configurable("")
        public int TP_absolute = 60; 
            
        @Configurable("")
        public int SL_absolute = 30; 

        @Configurable("")
        public int lvl_set_orders = 15; 

        @Configurable("")
        public int lvl_for_not_lose = 6; 
                        
        @Configurable("")
        public int lvl_not_lose = 1; 
 
        @Configurable("Slippage")
        public int slippage = 15;
        
         @Configurable("Label") 
        public String EA_name = "News_Orders";

        public double price_order, price_sl, price_tp;
        
    public int a = 1, b=0;    
  
//-----------------------------------------------------------------------    
public void onStart(IContext context) throws JFException {
    this.context = context;    
    this.engine = context.getEngine();    
    this.history = context.getHistory();
    this.console = context.getConsole();
    this.indicators = context.getIndicators();

    // определение pip и Digits
    pip = currentInstrument.getPipValue();
    if (pip == 0.0001) digits = 5;
    else if (pip == 0.01) digits = 3; 
    
    //--- здесь, при инициализации советника, проверяется правильность (допустимость) входных параметров

   
    if (Size_Lot < 0.001) {
        print("Ошибка! Устанолен размер базового лота меньше разрешенного брокером ");
        print("необходимо установить базовый лот не менее 0.001");
        context.stop();
    }
    

//--- вывод стартовой информации----
print ("Советник запущен. Инструмент торговли " + currentInstrument);

    return;
}
//------------------------------------------------------------------------
public void onTick(Instrument instrument, ITick tick) throws JFException {

    if (instrument != currentInstrument) {
        return;
    }    
    
    //--- for BUY  -------------------------- 
   if (buy == true) { 

price_order = round(tick.getAsk() + lvl_set_orders * pip,digits);
price_sl = round(price_order - SL_absolute * pip, digits);
price_tp = round(price_order + TP_absolute * pip, digits);
IOrder order = engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUYSTOP, Size_Lot, price_order, slippage, price_sl, price_tp);  
//   while (!IOrder.State.CREATED.equals(order.getState()) && !IOrder.State.CANCELED.equals(order.getState())) {
//      order.waitForUpdate(800);
//   }  
buy = false;        

   }   
 
//--- for SELL  -------------------------- 
  if (sell == true) { 

price_order = round(tick.getBid() - lvl_set_orders * pip,digits);
price_sl = round(price_order + SL_absolute * pip, digits);
price_tp = round(price_order - TP_absolute * pip, digits);
IOrder order = engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELLSTOP, Size_Lot, price_order, slippage, price_sl, price_tp);  
//   while (!IOrder.State.CREATED.equals(order.getState()) && !IOrder.State.CANCELED.equals(order.getState())) {
//      order.waitForUpdate(800);
//   }  
sell = false;        

   }    

//-------
if (b == 9) {
     for (IOrder order : engine.getOrders(instrument)) {    

print ("----------------------------------------------------------");
print ("order.getOrderCommand() = " + order.getOrderCommand());
print ("order.getState() = " + order.getState());
print ("----------------------------------------------------------");

     }   
a=0;
}



if (b == 30) { 
print ("b = " + b);
Close_All(instrument);
}
else {
if (b<30)    print ("b = " + b);
 
}
b++;

/*
and the last but not least void setStopLossPrice(double price,OfferSide side, double trailingStep)
lets you set the trailing step:
*/

//   order.setStopLossPrice(1.2222, OfferSide.BID, 20);


//-----



    return;
}

//------------------------------------------------------------------------ 
/*
public void open_order(Instrument instrument, ITick tick, IEngine.OrderCommand cmd, double Lots, double price, double sl, double tp) throws JFException {
   sl = round(sl,digits);
   tp = round(tp,digits);
   price = round(price,digits);
   if (sl == price) sl = 0;
   if (tp == price) tp = 0; 
   IOrder order = engine.submitOrder(getLabel(instrument), instrument, cmd, Lots, price, slippage, sl, tp);
   while (!IOrder.State.FILLED.equals(order.getState()) && !IOrder.State.CANCELED.equals(order.getState())) {
      order.waitForUpdate(800);
   }  
   return;
}
*/
//-------------------------------------------------------------
/*
public void ModifyTP(Instrument instrument) throws JFException {
        for (IOrder order : engine.getOrders(instrument)) {    
        if (buy_or_sell == 1 && order.getOrderCommand() == IEngine.OrderCommand.BUY &&
              order.getState() == IOrder.State.FILLED && isMine(order)) {
           if (order.getOpenPrice() != Find_open_price_highest(instrument)) {
               TP = round(TP, digits);
               order.setTakeProfitPrice(TP);
               order.waitForUpdate(5000);
           }
        }
        if (buy_or_sell == 0 && order.getOrderCommand() == IEngine.OrderCommand.SELL &&
              order.getState() == IOrder.State.FILLED && isMine(order)) {
           if (order.getOpenPrice() != Find_open_price_lowest(instrument)) {
               TP = round(TP, digits);
               order.setTakeProfitPrice(TP);
               order.waitForUpdate(5000);
           }
        }
     }    

   return;
}
//-------------------------------------------------------------------------

public void ModifySL(Instrument instrument) throws JFException {
     for (IOrder order : engine.getOrders(instrument)) {    
        if (buy_or_sell == 1 && order.getOrderCommand() == IEngine.OrderCommand.BUY &&
              order.getState() == IOrder.State.FILLED && isMine(order)) {
           
               SL = round(SL, digits);
               order.setStopLossPrice(SL);
               order.waitForUpdate(5000);
           
        }
        if (buy_or_sell == 0 && order.getOrderCommand() == IEngine.OrderCommand.SELL &&
              order.getState() == IOrder.State.FILLED && isMine(order)) {
          
               SL = round(SL, digits);
               order.setStopLossPrice(SL);
               order.waitForUpdate(5000);
           
        }
     }    
 
   return;
}
*/
//-------------------------------------------------------------------------

// double rounding
    public double round (double d, int precise) {
      double newDouble = new BigDecimal(d).setScale(precise, RoundingMode.HALF_UP).doubleValue();
       return newDouble;
    }
//------------------------------------------------------------------------
    private void print(String str) {
        console.getOut().println(str);}
//------------------------------------------------------------------------ 
    public boolean isMine(IOrder order) {                                  // проверка мейджика
        if (order.getLabel().startsWith(EA_name)) return (true);
        return (false);
    }
//------------------------------------------------------------------------
    protected String getLabel (Instrument instrument) throws JFException { // аналог мейджика
       int break_counter = 0;
       String label = instrument.name();    
       while (1 != 2) {
         break_counter = 0;     
         label = instrument.name();
         label = label.substring (0, 2) + label.substring (3, 5);
         label = label + (tagCounter++);
         label = label.toLowerCase();
         for (IOrder order : engine.getOrders(instrument)) 
            if (order.getLabel().equals(EA_name + label)) {
               tagCounter++;
               break_counter++;     
            }
         if (break_counter == 0) break;   
       }
      return EA_name + label;
    }
//------------------------------------------------------------------------   
public void Close_All(Instrument instrument) throws JFException {
     for (IOrder order : engine.getOrders(instrument)) {    
//        if (order.getOrderCommand() == IEngine.OrderCommand.SELLSTOP &&
//              order.getState() == IOrder.State.CREATED /*&& isMine(order)*/) {
print ("найден ордер  " + order.getOrderCommand() + "   " + order.getState() + " он будет закрыт");                  

            order.close();
//            order.waitForUpdate(5000);
//        } 
//        if (order.getOrderCommand() == IEngine.OrderCommand.BUYSTOP &&
//              order.getState() == IOrder.State.CREATED /*&& isMine(order)*/) {
// print ("найден ордер  " + order.getOrderCommand() + "   " + order.getState() + " он будет закрыт");                      
//            order.close();
//            order.waitForUpdate(5000);
//        }
     }   

     return;
}
//-------------------------------------------------------------
//-------------------------------------------------------------------------
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
    
    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onStop() throws JFException {
        
    }
}