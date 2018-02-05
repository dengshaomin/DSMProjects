package com.yizu.intelligentpiano.view;

import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import jp.kshoji.driver.midi.activity.AbstractSingleMidiActivity;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;

/**
 * Author：Created by liuxiaozhu on 2018/1/6.
 * Email: chenhuixueba@163.com
 */

public abstract class BasePullActivity extends AbstractSingleMidiActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        //隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        setData();
        setLinster();
    }

    protected abstract void initView();


    protected abstract void setData();

    protected abstract void setLinster();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    /**
     * midi连接
     */
    protected void onMidiCanInput(MidiInputDevice midiInputDevice) {
    }

    /**
     * midi断开
     */
    protected void onMidiNoInput(MidiInputDevice midiInputDevice) {
    }

    /**
     * 钢琴手指按下
     *
     * @param midiInputDevice 输入的设备
     * @param i
     * @param i1
     * @param i2              键数代表键的位置
     * @param i3
     */
    protected void onNoteOn(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }

    /**
     * 钢琴手指抬起
     *
     * @param midiInputDevice
     * @param i
     * @param i1
     * @param i2
     * @param i3
     */
    protected void onNoteOff(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
    }


    @Override
    public void onDeviceAttached(@NonNull UsbDevice usbDevice) {

    }

    @Override
    public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
        onMidiCanInput(midiInputDevice);
//        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已链接");
    }

    @Override
    public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {

    }

    @Override
    public void onDeviceDetached(@NonNull UsbDevice usbDevice) {

    }

    @Override
    public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
//        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已断开");
        onMidiNoInput(midiInputDevice);
    }


    @Override
    public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {

    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiCableEvents(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiSystemCommonMessage(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

    }

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
//        MyLogUtils.e(TAG, "onMidiNoteOn" + "NoteOn cable: " + i + ",  channel: " + i1 + ", note: " + i2 + ", velocity: " + i3);
        onNoteOff(midiInputDevice, i, i1, i2, i3);
    }


    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
//        MyLogUtils.e(TAG, "onMidiNoteOn" + "NoteOn cable: " + i + ",  channel: " + i1 + ", note: " + i2 + ", velocity: " + i3);
        onNoteOn(midiInputDevice, i, i1, i2, i3);
    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {

    }

    @Override
    public void onMidiSingleByte(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice midiInputDevice, int i) {

    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice midiInputDevice, int i) {

    }
}
