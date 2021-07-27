//
//  Request.swift
//  TestApp
//
//  Created by Jan Gaebel on 20.07.21.
//

import Foundation
import cognito_idp

public class Provider {
    var request: IdentityProviderClient = {
        let process = ProcessInfo.processInfo
        guard let region = process.environment["region"] else {
            fatalError("Missing region")
        }
        guard let clientId = process.environment["clientId"] else {
            fatalError("Missing clientId")
        }
        return IdentityProviderClient(region: region, clientId: clientId)
    }()
}
