using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Threadfix_Plugin
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void label1_Click(object sender, EventArgs e)
        {
            
        }

        private void button1_Click(object sender, EventArgs e)
        {
            String threadFixUrl = getThreadfixUrl();
            var apiKey = getApiKey();

            if (threadFixUrl != null && threadFixUrl != String.Empty && apiKey != null && apiKey != String.Empty)
            {
                if (ButtonPressed != null)
                {
                    ButtonPressed(this, null);
                }
                Close();
            }

        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        public String getThreadfixUrl()
        {
            return this.textBox1.Text;
        }

        public String getApiKey()
        {
            return this.textBox2.Text;
        }
    }
}
