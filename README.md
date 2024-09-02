# jharte-demo
Demonstrate how workload identity, iam, service account impersonation, and terraform can work together. 

## Initial Setup
Follow the [instructions](https://cloud.google.com/kubernetes-engine/docs/how-to/workload-identity#enable_on_cluster) to get workload identity enabled and set up. Verify your security section looks something like this. ![image](https://user-images.githubusercontent.com/15949789/149370829-478f1f62-bb12-46b8-9883-588761f353bd.png)

The nodes thmeselves don't have to be running using the same Google Service Acocunt used for workload identity. For instance, this setup in the hub services project ![image](https://user-images.githubusercontent.com/15949789/149371185-1b2e1140-bd3a-45e1-b28a-ad0242b28add.png)

Check that the workload has proper permission on the root service account. For the Kubernetes service account `atlantis` in namespace `atlantis` we are using the Google Service Account `svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com` but it could be anything. 
```
$ gcloud iam service-accounts get-iam-policy svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com
bindings:
- members:
  - serviceAccount:svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com
  role: roles/iam.serviceAccountTokenCreator
- members:
  - serviceAccount:eng-ops-hub-services-prod.svc.id.goog[atlantis/atlantis]
  - serviceAccount:liveramp-eng-ops.svc.id.goog[atlantis/atlantis]
  role: roles/iam.workloadIdentityUser
etag: BwXVa5yXsl0=
version: 1
```
From here, all that's needed for impersonation is the proper permissions. For this demo, I created two service accounts (which I will call target service accounts) and assigned the role `roles/iam.serviceAccountTokenCreator` to them. These service accounts can be in any project, and we do not need user managed service account keys to use them. 
 - jharte-demo-account-b@liveramp-eng.iam.gserviceaccount.com
 - jharte-demo-accout-a@liveramp-eng.iam.gserviceaccount.com

```
$ gcloud iam service-accounts add-iam-policy-binding jharte-demo-account-b@liveramp-eng.iam.gserviceaccount.com --project liveramp-eng-ops \                       
   --role roles/iam.serviceAccountTokenCreator \
   --member "serviceAccount:svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com"

$ gcloud iam service-accounts add-iam-policy-binding jharte-demo-accout-a@liveramp-eng.iam.gserviceaccount.com --project liveramp-eng-ops \                       
   --role roles/iam.serviceAccountTokenCreator \
   --member "serviceAccount:svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com"
```
At this point, all the plumbing is set up, all we need to do to use them in terraform is set up a special workflow. The google provider will use the `GOOGLE_IMPERSONATE_SERVICE_ACCOUNT` variable to do all the impersonation legwork in the background. With workflow identity setup, the default account will be `svc-atlantis-vault@liveramp-eng-ops.iam.gserviceaccount.com` which has the needed permission for impersonation. Therefore, to use the account you just need a modified workflow like this.

```    plan:
      steps:
         - run: terraform${ATLANTIS_TERRAFORM_VERSION} fmt -check=true -write=false -diff=true
         - env:
             name: GOOGLE_IMPERSONATE_SERVICE_ACCOUNT
             value: jharte-demo-accout-a@liveramp-eng.iam.gserviceaccount.com
         - init
         - plan
    apply:
      steps:
         - apply
```
From here, terraform will use the provider in the usual way. This shouldn't require any code changes from the consumer's perspective. 
    
