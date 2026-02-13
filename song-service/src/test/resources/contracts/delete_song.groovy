package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        url("/songs") {
            queryParameters {
                parameter("id", "1")
            }
        }
        method DELETE()
    }
    response {
        status OK()
        headers {
            contentType('application/json')
        }
        body(ids: [1])
    }
}
