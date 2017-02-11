#!/bin/bash -e

export TERM=xterm

export REGION="us-east-1"
export APP_NAME="${STACK_TYPE}"
export TF_STATE_STORE="variq-terraform"
export TF_STATE_FOLDER="/${STACK_TYPE}"


cd $APP_NAME

STATE_FILE="s3://${TF_STATE_STORE}${TF_STATE_FOLDER}/${STACK_NAME}"

count=$(aws s3 ls ${STATE_FILE} | wc -l)

if [ $count == 0 ]
         then
           echo "No state file is found for the provided stack name, ${STACK_NAME}"
           exit 0
        fi

    terraform remote config \
        -backend=s3 \
        -backend-config="bucket=${TF_STATE_STORE}" \
        -backend-config="key=${TF_STATE_FOLDER}/${STACK_NAME}" \
        -backend-config="region=us-east-1" \
        -backend-config="encrypt=1"


    terraform get

    ###find all SGs by tags and run
          asgs=$(aws autoscaling describe-tags --region ${REGION} --filters Name="Key",Values="stack" Name="Value",Values="${STACK_NAME}" | jq '.Tags[].ResourceId' -r)

          if [ -n "${asgs}" ]; then
            while read -r asg; do
              aws autoscaling resume-processes --region ${REGION} --auto-scaling-group-name $asg --scaling-processes Terminate
            done <<< "$asgs"
          fi

          terraform destroy -force

    if [[ $? -eq 0 ]]
    then
      status=SUCCESS
    else
      status=FAILURE
    fi

    if [[ "$status" == "SUCCESS" ]]
          then
            aws s3 rm ${STATE_FILE}
          else
            exit 1
          fi
