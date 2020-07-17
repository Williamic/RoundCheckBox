# RoundCheckBox
&emsp;&emsp;一个模仿Android原生控件动画效果的自定义圆形Checkbox

### 效果对比图
![效果对比图](https://raw.githubusercontent.com/Williamic/RoundCheckBox/master/view-RoundCheckBox_p9.gif)  
&emsp;&emsp;左边是原生的Checkbox，右边是RoundCheckbox，主要利用的是属性动画和Path来完成，可以自定义设置勾的颜色、背景选中和未选中时的颜色、透明度、水波纹颜色、开关等。具体实现方式和细节可以看[这里](https://williamic.github.io/article/view-RoundCheckBox/)
```
<declare-styleable name="RoundCheckBox">
        <attr name="rcb_colorTick" format="integer" />              //勾的颜色
        <attr name="rcb_colorSelected" format="integer" />          //选中时颜色
        <attr name="rcb_colorUnSelected" format="integer" />        //取消选中时颜色
        <attr name="rcb_colorRippleSelected" format="integer" />    
        <attr name="rcb_colorRippleUnSelected" format="integer" />
        <attr name="rcb_colorSelectedAlpha" format="float"/>
        <attr name="rcb_colorUnSelectedAlpha" format="float"/>
        <attr name="rcb_startAnimDuration" format="integer" />       //选中时动画时间
        <attr name="rcb_endAnimDuration" format="integer" />         //取消选中时动画时间
        <attr name="rcb_showRipple" format="boolean" />              //是否启用水波纹，默认为true
        <attr name="rcb_checked" format="boolean" />                 //设置初始状态，默认为false
        <attr name="rcb_rippleAlpha" format="float"/>                //水波纹的透明度 默认15%
    </declare-styleable>
```
