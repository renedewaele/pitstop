import * as core from '@actions/core';
import {HttpClient} from '@actions/http-client';

async function run() {
    try {
        // Get inputs
        const metadata = core.getInput('metadata-json', {required: true})
        const token = core.getInput('token', {required: true});
        const clusterName = core.getInput('cluster-name', {required: true});
        const applicationName = core.getInput('application-name', {required: true});
        const firstTag = JSON.parse(metadata).tags[0];
        const tagParts = firstTag.split(':');
        const imageName = tagParts[0].substring(tagParts[0].lastIndexOf('/') + 1);
        const version = tagParts[1];

        const httpClient = new HttpClient();

        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        const body = {
            clusterName: clusterName,
            applicationName: applicationName,
            imageName: imageName,
            version: version
        }

        console.log(`Deploying ${applicationName} to cluster ${clusterName} using image ${imageName}:${version}`);

        const response = await httpClient.post("https://api.dashboard.flux.host/deploy-application", JSON.stringify(body), headers);
        const responseBody = await response.readBody();
        const statusCode = response.message.statusCode;
        if (typeof statusCode !== 'number' || statusCode < 200 || statusCode >= 300) {
            core.setFailed(`Request failed with status code: ${response.message.statusCode}`);
            console.log(responseBody);
        } else {
            console.log('Request succeeded:', responseBody);
        }
    } catch (error) {
        core.setFailed(`Action failed with error: ${error}`);
    }
}

run();
