//
//  TestAppTests.swift
//  TestAppTests
//
//  Created by Jan Gaebel on 20.07.21.
//

import XCTest
import cognito_idp
@testable import TestApp

class RequestTest: XCTestCase {
    let sut = Provider()

    func testProviderCanExecuteRequest() {
        let expectation = self.expectation(description: "Awaiting cognito-idp request completion")
        sut.request.signIn(username: "Username", password: "Password123!") { result, _ in
            expectation.fulfill()
            result?.fold(
                onSuccess: { result in
                    XCTFail("Request should fail.")
                },
                onFailure: { error in
                    XCTAssertTrue(error is IdentityProviderException.NotAuthorized)
                }
            )
        }

        waitForExpectations(timeout: 10)
    }
}
