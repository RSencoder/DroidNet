package com.mingxiangChen.droidnet;

/**
 * Interface definition for a {@link com.github.megatronking.netbare.gateway.VirtualGateway} that 
 * monitors user operations on DroidNet.
 * 
 * @author RSmxchen
 * @since 2019-05-01 15:41
 */
public interface SwitcherListener {
    
    /**
     * Callback method to be invoked when the user clicks capture start button.
     */
    void captureOn();

    /**
     * Callback method to be invoked when the user clicks capture stop button.
     */
    void captureOff();

    /**
     * Callback method to be invoked when the user clicks firewall start button.
     */
    void firewallOn();

    /**
     * Callback method to be invoked when the user clicks firewall stop button.
     */
    void firewallOff();
}
