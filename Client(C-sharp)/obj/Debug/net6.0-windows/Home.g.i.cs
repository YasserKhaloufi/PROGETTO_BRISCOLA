﻿#pragma checksum "..\..\..\Home.xaml" "{ff1816ec-aa5e-4d10-87f7-6f4963833460}" "F85F21C98017C6F8CEC65DC87E742E78097A6EE1"
//------------------------------------------------------------------------------
// <auto-generated>
//     Il codice è stato generato da uno strumento.
//     Versione runtime:4.0.30319.42000
//
//     Le modifiche apportate a questo file possono provocare un comportamento non corretto e andranno perse se
//     il codice viene rigenerato.
// </auto-generated>
//------------------------------------------------------------------------------

using Client_C_sharp_;
using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Automation;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Controls.Ribbon;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Effects;
using System.Windows.Media.Imaging;
using System.Windows.Media.Media3D;
using System.Windows.Media.TextFormatting;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Shell;


namespace Client_C_sharp_ {
    
    
    /// <summary>
    /// Home
    /// </summary>
    public partial class Home : System.Windows.Window, System.Windows.Markup.IComponentConnector {
        
        
        #line 9 "..\..\..\Home.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.Grid GridStart;
        
        #line default
        #line hidden
        
        
        #line 15 "..\..\..\Home.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox txtNome;
        
        #line default
        #line hidden
        
        
        #line 17 "..\..\..\Home.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.Button btnStart;
        
        #line default
        #line hidden
        
        
        #line 18 "..\..\..\Home.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.Button btnImpostazioni;
        
        #line default
        #line hidden
        
        private bool _contentLoaded;
        
        /// <summary>
        /// InitializeComponent
        /// </summary>
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        [System.CodeDom.Compiler.GeneratedCodeAttribute("PresentationBuildTasks", "6.0.0.0")]
        public void InitializeComponent() {
            if (_contentLoaded) {
                return;
            }
            _contentLoaded = true;
            System.Uri resourceLocater = new System.Uri("/Client(C-sharp);component/home.xaml", System.UriKind.Relative);
            
            #line 1 "..\..\..\Home.xaml"
            System.Windows.Application.LoadComponent(this, resourceLocater);
            
            #line default
            #line hidden
        }
        
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        [System.CodeDom.Compiler.GeneratedCodeAttribute("PresentationBuildTasks", "6.0.0.0")]
        [System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Never)]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Maintainability", "CA1502:AvoidExcessiveComplexity")]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1800:DoNotCastUnnecessarily")]
        void System.Windows.Markup.IComponentConnector.Connect(int connectionId, object target) {
            switch (connectionId)
            {
            case 1:
            this.GridStart = ((System.Windows.Controls.Grid)(target));
            return;
            case 2:
            this.txtNome = ((System.Windows.Controls.TextBox)(target));
            
            #line 15 "..\..\..\Home.xaml"
            this.txtNome.GotFocus += new System.Windows.RoutedEventHandler(this.txtNome_GotFocus);
            
            #line default
            #line hidden
            
            #line 15 "..\..\..\Home.xaml"
            this.txtNome.LostFocus += new System.Windows.RoutedEventHandler(this.txtNome_LostFocus);
            
            #line default
            #line hidden
            return;
            case 3:
            this.btnStart = ((System.Windows.Controls.Button)(target));
            
            #line 17 "..\..\..\Home.xaml"
            this.btnStart.Click += new System.Windows.RoutedEventHandler(this.btnStart_Click);
            
            #line default
            #line hidden
            return;
            case 4:
            this.btnImpostazioni = ((System.Windows.Controls.Button)(target));
            
            #line 18 "..\..\..\Home.xaml"
            this.btnImpostazioni.Click += new System.Windows.RoutedEventHandler(this.btnImpostazioni_Click);
            
            #line default
            #line hidden
            return;
            }
            this._contentLoaded = true;
        }
    }
}

