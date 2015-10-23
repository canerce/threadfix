using System;
using System.Collections.Generic;
using System.Windows.Forms;

using AppScan;
using AppScan.Events;
using AppScan.Extensions;
using AppScan.Scan.Data;
using Threadfix_Plugin;

namespace ThreadFixExtensionPlugin
{
/// <summary>
/// GuiDemo main implementation class.
/// implementing the IExtensionLogic interface
/// </summary>
public class ThreadFixExtension : IExtensionLogic
{

    public static string apiKey { get; set; }
    public static string threadFixUrl { get; set; }
    public static ThreadFixApi api { get; set; }
#region Initialization

/// <summary>
/// extension initialization. typically called on AppScan's startup
/// </summary>
/// <param name="appScan">main application object the extension is loaded into</param>
/// <param name="extensionDir">extension's working directory</param>
public void Load
(IAppScan appscan, IAppScanGui appScanGui, string extensionDir)
{
InitGuiHooks();
RegisterGuiHooks(appScanGui);

}



/// <summary>
/// Creates the menu entries objects
/// </summary>
private void InitGuiHooks()
{
extMenuItems = CreateMenuItems();
// Create a (Tools->Extension) menu entry collection
IssueMenuItems = CreateIssueContextMenuItems(); 
// Create a context-menu entry collection
}
/// <summary>
/// Add menu entries to AppScan
/// </summary>
/// <param name="appScanGui"></param>
private void RegisterGuiHooks(IAppScanGui appScanGui)
{
foreach 
(IMenuItem<EventArgs> item in extMenuItems)
appScanGui.ExtensionsMenu.Add(item);

foreach
(IMenuItem<IssuesEventArgs> item in IssueMenuItems)
appScanGui.IssueContextMenu.Add(item);
}

#endregion Initialization

#region GUI itmes construction

private ICollection<IMenuItem<EventArgs>> CreateMenuItems()
{
mainExtMenuItem = new MenuItem
<EventArgs>(messagePrefix + "Set configurations", DelegateEmpty);
List<IMenuItem<EventArgs>> items =
new List<IMenuItem<EventArgs>>();
items.Add(mainExtMenuItem);
return items;

} 
private ICollection<IMenuItem<IssuesEventArgs>> CreateIssueContextMenuItems()

{ 
mainIssuesExtMenuItem =
new MenuItem<IssuesEventArgs>(messagePrefix + "Operation 1", DelegateIssue1);
List<IMenuItem<IssuesEventArgs>> items =
new List<IMenuItem<IssuesEventArgs>>();
items.Add(mainIssuesExtMenuItem);
return items;
}
#endregion GUI itmes construction
#region delegates

//TODO: remove this
//Strictly used for a quick dialog to help debug
public static string ShowDialog(string text, string caption)
{
    Form prompt = new Form();
    prompt.Width = 500;
    prompt.Height = 150;
    prompt.FormBorderStyle = FormBorderStyle.FixedDialog;
    prompt.Text = caption;
    prompt.StartPosition = FormStartPosition.CenterScreen;
    Label textLabel = new Label() { Left = 50, Top = 20, Text = text };
    TextBox textBox = new TextBox() { Left = 50, Top = 50, Width = 400 };
    Button confirmation = new Button() { Text = "Ok", Left = 350, Width = 100, Top = 70, DialogResult = DialogResult.OK };
    confirmation.Click += (sender, e) => { prompt.Close(); };
    prompt.Controls.Add(textBox);
    prompt.Controls.Add(confirmation);
    prompt.Controls.Add(textLabel);
    prompt.AcceptButton = confirmation;
    return prompt.ShowDialog() == DialogResult.OK ? textBox.Text : "";
}
/// <summary>
/// Tools-Extensions menu entry action
/// </summary>
/// <param name="args"></param>
private static void DelegateEmpty(EventArgs args)
{
    Form1 threadfixForm = new Form1();
    threadfixForm.ButtonPressed += threadfixForm_ButtonPressed;
    threadfixForm.ShowDialog();
    apiKey = threadfixForm.getApiKey();
    threadFixUrl = threadfixForm.getThreadfixUrl();
    //TODO: Remove this
    Console.Write("Api Key: " + threadfixForm.getApiKey());
    Console.Write("Threadfix Url: " + threadfixForm.getThreadfixUrl()); 
}

private static void threadfixForm_ButtonPressed(object sender, EventArgs e)
{
   var applications =  api.GetThreadFixApplications();
   foreach (ApplicationInfo application in applications)
   {
       Console.Write("Application name: " + application.ApplicationName);
       Console.Write("ApplicationId: " + application.ApplicationId);
       Console.Write("Organization Name: " + application.OrganizationName);
   }
}

/// <summary>
/// Issue-context menu entry action
/// </summary>
/// <param name="args"></param>
private void DelegateIssue1(IssuesEventArgs args)
{
DoDelegateIssue(1, args.issues);

}

private static void DoDelegateIssue(int num, ICollection<IIssue> issues)
{
if (issues != null)
{
MessageBox.Show
("Issue operation " + num.ToString()
+ " issues count: " + issues.Count, messageTitle);
}
else
MessageBox.Show
("Issue operation " + num.ToString()
+ " issues list is empty", messageTitle);
}
#endregion delegates
#region data members
IMenuItem<EventArgs> mainExtMenuItem;
IMenuItem<IssuesEventArgs> mainIssuesExtMenuItem;
ICollection<IMenuItem<EventArgs>> extMenuItems;
ICollection<IMenuItem<IssuesEventArgs>> IssueMenuItems;

const string messageTitle = "Threadfix";
const string messagePrefix = "Threadfix: ";

#endregion data members

#region other

/// <summary>
/// retrieves data about current available ext-version
/// </summary>
/// <param name="targetApp">app this extension is designated for</param>
/// <param name="targetAppVersion">current version of targetApp</param>
/// <returns>
///update data of most recent extension version,
///or null if no data was found, or feature isn't supported.
///It is valid to return update data of current version.
///extension-update will take place only if returned value indicates
///a newer version
///</returns>
public ExtensionVersionInfo GetUpdateData
(Edition targetApp, System.Version targetAppVersion)
{
return null;
}

#endregion other

}
}