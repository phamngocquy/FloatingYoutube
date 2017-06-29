package com.example.quypn.myapplication;



public interface FloatyOrientationListener {


    /**
     *
     * This method is called before the orientation change happens, you can use this to save the data of your views so you can later populate the data back in {@link #afterOrientationChange}
     *
     * @param floaty The floating window
     *
     */
    void beforeOrientationChange(Floaty floaty);

    /**
     * This method is called after the orientation change happens, you can use this to restore the data of your views that you saved in {@link #beforeOrientationChange}
     *
     * @param floaty The floating window
     *
     */
    void afterOrientationChange(Floaty floaty);

}
