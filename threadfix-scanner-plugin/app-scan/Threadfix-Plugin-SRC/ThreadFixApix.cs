using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using RestSharp;
using ThreadFixExtensionPlugin;

namespace Threadfix_Plugin
{
    public class ThreadFixApi
    {

        private static readonly string ApplicationsResource = "code/applications";
        private static readonly string VulnerabilitiesResource = "code/markers/{AppId}";
        private static readonly string RootElement = "object";
        private static readonly string ApiKeyParameter = "apiKey";
        private string apiKey ="";
        private string threadfixUrl = "";

        public ThreadFixApi(string apiKey, string threadfixUrl)
        {
            this.apiKey = apiKey;
            this.threadfixUrl = threadfixUrl;
        }

        public List<ApplicationInfo> GetThreadFixApplications()
        {
            var request = new RestRequest()
            {
                Resource = ApplicationsResource
            };

            return Execute<List<ApplicationInfo>>(request);
        }

        //Change
        /*public List<VulnerabilityMarker> GetVulnerabilityMarkers(string appId)
        {
            var request = new RestRequest()
            {
                Resource = VulnerabilitiesResource
            };

            request.AddParameter("AppId", appId, ParameterType.UrlSegment);

            return Execute<List<VulnerabilityMarker>>(request);
        }*/

        public T Execute<T>(RestRequest request) where T : new()
        {
            var client = new RestClient();
            client.BaseUrl = new Uri(threadfixUrl);

            request.AddParameter(ApiKeyParameter, apiKey);
            request.RequestFormat = DataFormat.Json;
            request.RootElement = RootElement;

            var response = client.Execute<T>(request);
            if (response.ErrorException != null)
            {
                throw new ApplicationException(response.ErrorException.InnerException.Message, response.ErrorException);
            }

            // TODO: Serialize the entire response intead of just "object" and check for errors sent back from threadfix api

            return response.Data;
        }
    }
}
