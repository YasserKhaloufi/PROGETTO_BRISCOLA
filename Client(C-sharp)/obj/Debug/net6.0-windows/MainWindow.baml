   M S B A M L   `   `   `  ����J  FClient(C-sharp), Version=1.0.0.0, Culture=neutral, PublicKeyToken=null1clr-namespace:Client_C_sharp_Client_C_sharp_  5   	        Client_C_sharp_.MainWindow   5   	   W SPresentationCore, Version=6.0.2.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=6.0.2.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35e aSystem.Windows.Controls.Ribbon, Version=6.0.2.0, Culture=neutral, PublicKeyToken=b77a5c561934e089R NWindowsBase, Version=6.0.2.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35F 9http://schemas.microsoft.com/winfx/2006/xaml/presentation     5   	   R NSystem.Xaml, Version=6.0.2.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   @d2http://schemas.microsoft.com/expression/blend/2008     5   	   Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5   	   /localclr-namespace:Client_C_sharp_     5   	     � Title$  Briscola��5   	    � Icon$ img/Icon.png��6    � WindowStyle$ SingleBorderWindow=�5	   	    � WindowState$ 	Maximized=�5
   	   .��5      � $
��1188��6   $��Center=�6   .+�5   
   �� -   5         ��Name$ ItemsControl��5   �   $
��Left=�64   $	��300��6O   $	��Top=�6\   $��496,772,0,0q�6t   ��6   �� *Mano+��5      �� 5      .R�5      �� -   5        $	��300��5      $	��200��6*   .��5      �� $	��300��6    $	��200��6-   ��69   �� *
Img_path+5      5      ��5      �� 5      .R�5      ��  �� Orientation$ 
Horizontal=�6!   5      5      �� 5   
   $
��Left=�6   $	��300��6+   $��
494,78,0,0q�68   $	��Top=�6L   $	��200��6d   $��img/carta-retro.jpg��6p   $
��Fill=�6�   �� 5   
   $
��Left=�6   $	��300��6+   $��
694,78,0,0q�68   $	��Top=�6L   $	��200��6d   $��img/carta-retro.jpg��6p   $
��Fill=�6�   �� 5   
   $
��Left=�6   $	��300��6+   $��
894,78,0,0q�68   $	��Top=�6L   $	��200��6d   $��img/carta-retro.jpg��6p   $
��Fill=�6�   �� 5    
   $��Briscola��6   $
��Left=�6#   $��66��6>   $��216,339,0,0q�6J   $	��Top=�6_   $	��120��6w   $��35(�6�   ���	#FF128A01$��Bernard MT Condensed)�6�   �� 5!   
   -   5         ��Name$ imgBoxBriscola��5!      $
��Left=�6(   $	��300��6C   $��178,405,0,0q�6P   $	��Top=�6e   $	��200��6}   ��6�   �� *Briscola.Img_path+�� 5#   
   -   5         ��Name$ txtDebug��5#   �   $
��Left=�6   $	��320��6-   $��124,762,0,0q�6:    �� TextWrapping$
 Wrap=�6O   $	��Top=�6c   $	��304��6{   	 J� RenderTransformOrigin$	 0.837,0.536&�6�   
 �� 
IsReadOnly$

 True��6�   .��6�   ��5$      �� 5%      .��5&   "   5'      �� 5)   
   -   5        $ itemSource2��5)   �   $
��Left=�6<   $	��300��6W   $��694,405,0,0q�6d   $	��Top=�6y   $	��200��6�   ��6   �� *CarteGiocate+��5*      �� 5+      .R�5,      �� $	��300��6    $	��200��6-   ��69   �� *
Img_path+5-      ��5/      �� 50      .R�51      �� $ 
Horizontal=�6!   52      54      �� 56   
   -   5        $ txtScore��56      $
��Left=�6$   $��216,725,0,0q�6?   $
 Wrap=�6T   $	��Top=�6h   $	��120��6�   $

 True��6�   $��0��6�   57      ��59      �� 5:   
    �� ImageSource$ img/Wooden table.jpg��6    j� Stretch$ UniformToFill=�68   5<      